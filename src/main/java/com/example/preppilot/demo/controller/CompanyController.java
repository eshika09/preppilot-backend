package com.example.preppilot.demo.controller;

import com.example.preppilot.demo.dto.request.CompanyRequest;
import com.example.preppilot.demo.dto.response.CompanyResponse;
import com.example.preppilot.demo.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {
    private final CompanyService companyService;

    @PostMapping
    public ResponseEntity<CompanyResponse> addCompany(@Valid @RequestBody CompanyRequest request) {
        return ResponseEntity.ok(companyService.addCompany(request));
    }

    @GetMapping
    public ResponseEntity<List<CompanyResponse>> getAllCompanies() {
        return ResponseEntity.ok(companyService.getAllCompanies());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanyResponse> getCompany(@PathVariable Long id) {
        return ResponseEntity.ok(companyService.getCompany(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompanyResponse> updateCompany(@PathVariable Long id,
                                                         @Valid @RequestBody CompanyRequest request) {
        return ResponseEntity.ok(companyService.updateCompany(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCompany(@PathVariable Long id) {
        companyService.deleteCompany(id);
        return ResponseEntity.ok("Company deleted successfully");
    }
}
