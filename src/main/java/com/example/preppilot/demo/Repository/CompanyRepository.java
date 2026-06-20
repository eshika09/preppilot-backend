package com.example.preppilot.demo.Repository;

import com.example.preppilot.demo.entity.Company;
import com.example.preppilot.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    // get all companies for a user
    List<Company> findByUserOrderByCreatedAtDesc(User user);

    // get one company — but only if it belongs to this user (security)
    Optional<Company> findByIdAndUser(Long id, User user);

    // used later by @Scheduled deadline reminder
    List<Company> findByDeadlineAndUser(LocalDate deadline, User user);
}
