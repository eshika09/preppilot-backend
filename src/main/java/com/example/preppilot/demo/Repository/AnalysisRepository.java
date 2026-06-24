package com.example.preppilot.demo.Repository;

import com.example.preppilot.demo.entity.Analysis;
import com.example.preppilot.demo.entity.Company;
import com.example.preppilot.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnalysisRepository extends JpaRepository<Analysis, Long> {
    List<Analysis> findByUserAndCompanyOrderByCreatedAtDesc(User user, Company company);
    int countByUser(User user);
}
