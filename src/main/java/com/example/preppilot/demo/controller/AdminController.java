package com.example.preppilot.demo.controller;

import com.example.preppilot.demo.Repository.UserRepository;
import com.example.preppilot.demo.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserRepository userRepository;

    // only ADMIN can access this
    @GetMapping("/users")
    public ResponseEntity<List<Map<String, Object>>> getAllUsers() {
        List<Map<String, Object>> users = userRepository.findAll()
                .stream()
                .map(user -> {
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("id", user.getId());
                    userMap.put("name", user.getName());
                    userMap.put("email", user.getEmail());
                    userMap.put("isPremium", user.isPremium());
                    userMap.put("analysisCount", user.getAnalysisCount());
                    userMap.put("role", user.getRole());
                    userMap.put("createdAt", user.getCreatedAt().toString());
                    return userMap;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }
    // promote a user to admin
    @PutMapping("/users/{id}/make-admin")
    public ResponseEntity<String> makeAdmin(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole("ADMIN");
        userRepository.save(user);
        return ResponseEntity.ok("User promoted to ADMIN");
    }

    // manually set premium (useful for testing)
    @PutMapping("/users/{id}/set-premium")
    public ResponseEntity<String> setPremium(@PathVariable Long id,
                                             @RequestParam boolean premium) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPremium(premium);
        userRepository.save(user);
        return ResponseEntity.ok("User premium status updated to: " + premium);
    }
}
