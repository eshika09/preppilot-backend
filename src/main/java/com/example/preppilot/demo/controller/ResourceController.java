package com.example.preppilot.demo.controller;

import com.example.preppilot.demo.dto.request.ResourceRequest;
import com.example.preppilot.demo.dto.response.BookmarkedResourceResponse;
import com.example.preppilot.demo.dto.response.ResourceResponse;
import com.example.preppilot.demo.entity.ResourceType;
import com.example.preppilot.demo.service.ResourceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ResourceController {
    private final ResourceService resourceService;

    // For LINK and NOTE (no file)
    @PostMapping("/api/resources/text")
    public ResponseEntity<ResourceResponse> addTextResource(@Valid @RequestBody ResourceRequest request) {
        return ResponseEntity.ok(resourceService.addTextResource(request));
    }

    // For PDF, IMAGE, CODE, ZIP (file upload)
    @PostMapping("/api/resources/file")
    public ResponseEntity<ResourceResponse> addFileResource(
            @RequestParam("title") String title,
            @RequestParam("type") ResourceType type,
            @RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(resourceService.addFileResource(title, type, file));
    }

    // Get all resources
    @GetMapping("/api/resources")
    public ResponseEntity<List<ResourceResponse>> getAllResources() {
        return ResponseEntity.ok(resourceService.getAllResources());
    }

    // Delete a resource
    @DeleteMapping("/api/resources/{id}")
    public ResponseEntity<String> deleteResource(@PathVariable Long id) {
        resourceService.deleteResource(id);
        return ResponseEntity.ok("Resource deleted successfully");
    }

    // Bookmark resource to company
    @PostMapping("/api/resources/{resourceId}/bookmark/{companyId}")
    public ResponseEntity<String> bookmarkResource(
            @PathVariable Long resourceId,
            @PathVariable Long companyId) {
        return ResponseEntity.ok(resourceService.bookmarkResource(resourceId, companyId));
    }

    // Remove bookmark
    @DeleteMapping("/api/resources/{resourceId}/bookmark/{companyId}")
    public ResponseEntity<String> removeBookmark(
            @PathVariable Long resourceId,
            @PathVariable Long companyId) {
        resourceService.removeBookmark(resourceId, companyId);
        return ResponseEntity.ok("Bookmark removed successfully");
    }

    // Get all bookmarked resources for a company
    @GetMapping("/api/companies/{companyId}/resources")
    public ResponseEntity<List<BookmarkedResourceResponse>> getResourcesForCompany(
            @PathVariable Long companyId) {
        return ResponseEntity.ok(resourceService.getResourcesForCompany(companyId));
    }
}
