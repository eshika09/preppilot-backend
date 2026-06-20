package com.example.preppilot.demo.Repository;

import com.example.preppilot.demo.entity.Company;
import com.example.preppilot.demo.entity.Resource;
import com.example.preppilot.demo.entity.ResourceBookmark;
import com.example.preppilot.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResourceBookmarkRepository extends JpaRepository<ResourceBookmark, Long> {
    // get all bookmarks for a specific company
    List<ResourceBookmark> findByCompanyAndUser(Company company, User user);

    // check if already bookmarked
    Optional<ResourceBookmark> findByResourceAndCompany(Resource resource, Company company);

    // when a resource is deleted, clean up its bookmarks
    void deleteByResource(Resource resource);
}
