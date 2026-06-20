package com.example.preppilot.demo.service;

import com.example.preppilot.demo.Repository.CompanyRepository;
import com.example.preppilot.demo.Repository.ResourceBookmarkRepository;
import com.example.preppilot.demo.Repository.ResourceRepository;
import com.example.preppilot.demo.Repository.UserRepository;
import com.example.preppilot.demo.config.FileStorageConfig;
import com.example.preppilot.demo.dto.request.ResourceRequest;
import com.example.preppilot.demo.dto.response.BookmarkedResourceResponse;
import com.example.preppilot.demo.dto.response.ResourceResponse;
import com.example.preppilot.demo.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResourceService {
    private final ResourceRepository resourceRepository;
    private final ResourceBookmarkRepository bookmarkRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    private User getLoggedInUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    private ResourceResponse toResponse(Resource resource) {
        return ResourceResponse.builder()
                .id(resource.getId())
                .title(resource.getTitle())
                .type(resource.getType())
                .url(resource.getUrl())
                .createdAt(resource.getCreatedAt())
                .build();
    }

    // Add a LINK or NOTE resource (no file upload needed)
    @Transactional
    public ResourceResponse addTextResource(ResourceRequest request) {
        User user = getLoggedInUser();

        if (request.getType() == ResourceType.LINK) {
            if (request.getUrl() == null || request.getUrl().isBlank()) {
                throw new RuntimeException("URL is required for LINK type");
            }
        }

        if (request.getType() == ResourceType.NOTE) {
            if (request.getUrl() == null || request.getUrl().isBlank()) {
                throw new RuntimeException("Note content is required for NOTE type");
            }
        }

        Resource resource = Resource.builder()
                .title(request.getTitle())
                .type(request.getType())
                .url(request.getUrl()) // for NOTE type, url stores the note text
                .user(user)
                .build();

        return toResponse(resourceRepository.save(resource));
    }

    // Add a FILE resource (PDF, IMAGE, CODE, ZIP)
    @Transactional
    public ResourceResponse addFileResource(String title,
                                            ResourceType type,
                                            MultipartFile file) throws IOException {
        User user = getLoggedInUser();

        // validate type is a file type
        if (type == ResourceType.LINK || type == ResourceType.NOTE) {
            throw new RuntimeException("Use the /link endpoint for LINK and NOTE types");
        }

        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = (originalFilename != null && originalFilename.contains("."))
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";

        String fileName = UUID.randomUUID() + extension;
        Path filePath = Paths.get(uploadDir, fileName);
        Files.write(filePath, file.getBytes());

        Resource resource = Resource.builder()
                .title(title)
                .type(type)
                .url(filePath.toString())
                .user(user)
                .build();

        return toResponse(resourceRepository.save(resource));
    }

    // Get all resources for logged in user
    public List<ResourceResponse> getAllResources() {
        User user = getLoggedInUser();
        return resourceRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // Delete a resource
    @Transactional
    public void deleteResource(Long id) {
        User user = getLoggedInUser();
        Resource resource = resourceRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Resource not found or access denied"));

        // if PDF, delete the file from disk too
        if (resource.getType() == ResourceType.PDF) {
            try {
                Files.deleteIfExists(Paths.get(resource.getUrl()));
            } catch (IOException e) {
                // log but don't fail — DB cleanup is more important
                System.err.println("Could not delete file: " + resource.getUrl());
            }
        }

        bookmarkRepository.deleteByResource(resource);
        resourceRepository.delete(resource);
    }

    // Bookmark a resource to a company
    @Transactional
    public String bookmarkResource(Long resourceId, Long companyId) {
        User user = getLoggedInUser();

        Resource resource = resourceRepository.findByIdAndUser(resourceId, user)
                .orElseThrow(() -> new RuntimeException("Resource not found or access denied"));

        Company company = companyRepository.findByIdAndUser(companyId, user)
                .orElseThrow(() -> new RuntimeException("Company not found or access denied"));

        // check if already bookmarked
        if (bookmarkRepository.findByResourceAndCompany(resource, company).isPresent()) {
            throw new RuntimeException("Resource already bookmarked for this company");
        }

        ResourceBookmark bookmark = ResourceBookmark.builder()
                .resource(resource)
                .company(company)
                .user(user)
                .build();

        bookmarkRepository.save(bookmark);
        return "Resource bookmarked successfully";
    }

    // Remove bookmark
    @Transactional
    public void removeBookmark(Long resourceId, Long companyId) {
        User user = getLoggedInUser();

        Resource resource = resourceRepository.findByIdAndUser(resourceId, user)
                .orElseThrow(() -> new RuntimeException("Resource not found or access denied"));

        Company company = companyRepository.findByIdAndUser(companyId, user)
                .orElseThrow(() -> new RuntimeException("Company not found or access denied"));

        ResourceBookmark bookmark = bookmarkRepository.findByResourceAndCompany(resource, company)
                .orElseThrow(() -> new RuntimeException("Bookmark not found"));

        bookmarkRepository.delete(bookmark);
    }

    // Get all bookmarked resources for a company
    public List<BookmarkedResourceResponse> getResourcesForCompany(Long companyId) {
        User user = getLoggedInUser();

        Company company = companyRepository.findByIdAndUser(companyId, user)
                .orElseThrow(() -> new RuntimeException("Company not found or access denied"));

        return bookmarkRepository.findByCompanyAndUser(company, user)
                .stream()
                .map(b -> BookmarkedResourceResponse.builder()
                        .bookmarkId(b.getId())
                        .resourceId(b.getResource().getId())
                        .title(b.getResource().getTitle())
                        .type(b.getResource().getType())
                        .url(b.getResource().getUrl())
                        .bookmarkedAt(b.getBookmarkedAt())
                        .build())
                .collect(Collectors.toList());
    }
}
