package com.example.preppilot.demo.Repository;

import com.example.preppilot.demo.entity.Resource;
import com.example.preppilot.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {
    List<Resource> findByUserOrderByCreatedAtDesc(User user);

    Optional<Resource> findByIdAndUser(Long id, User user);
}
