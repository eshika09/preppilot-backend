package com.example.preppilot.demo.Repository;

import com.example.preppilot.demo.entity.CommunityExperience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityExperienceRepository extends JpaRepository<CommunityExperience, Long> {
    // fetch all experiences for a company name (case insensitive)
    // this is what AI reads for prep plan
    List<CommunityExperience> findByCompanyNameIgnoreCaseOrderByCreatedAtDesc(String companyName);

    // fetch experiences by company + round type
    List<CommunityExperience> findByCompanyNameIgnoreCaseAndRoundIgnoreCaseOrderByCreatedAtDesc(
            String companyName, String round);
}
