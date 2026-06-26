package com.example.preppilot.demo.Repository;

import com.example.preppilot.demo.entity.Company;
import com.example.preppilot.demo.entity.CompanyPriority;
import com.example.preppilot.demo.entity.CompanyStatus;
import com.example.preppilot.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    // search by name (case insensitive) — used for search feature
    List<Company> findByUserAndNameContainingIgnoreCaseOrderByCreatedAtDesc(
            User user, String name);

    // filter by status
    List<Company> findByUserAndStatusOrderByCreatedAtDesc(
            User user, CompanyStatus status);

    // filter by priority
    List<Company> findByUserAndPriorityOrderByCreatedAtDesc(
            User user, CompanyPriority priority);

    // filter by status AND priority combined
    List<Company> findByUserAndStatusAndPriorityOrderByCreatedAtDesc(
            User user, CompanyStatus status, CompanyPriority priority);

    // used by dashboard — count per status
    @Query("SELECT c.status, COUNT(c) FROM Company c WHERE c.user = :user GROUP BY c.status")
    List<Object[]> countByStatusForUser(@Param("user") User user);

    // used by dashboard — upcoming deadlines
    List<Company> findByUserAndDeadlineBetweenOrderByDeadlineAsc(
            User user, LocalDate from, LocalDate to);


    // Find all companies with deadline = specific date across ALL users
// used by scheduler to find tomorrow's deadlines
    @Query("SELECT c FROM Company c WHERE c.deadline = :deadline")
    List<Company> findAllByDeadline(@Param("deadline") LocalDate deadline);
}
