package com.stockflow.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.stockflow.model.Company;
import com.stockflow.repository.CompanyRepository;

import java.util.Map;

@RestController
@RequestMapping("/company")
public class CompanyController {

    @Autowired
    private CompanyRepository companyRepository;

    @PostMapping("/register")
    public Company registerCompany(@RequestBody Company company) {
        return companyRepository.save(company);
    }

    // Called by dashboard to show company name in header
    @GetMapping("/{id}/name")
    public ResponseEntity<?> getCompanyName(@PathVariable Long id) {
        return companyRepository.findById(id)
                .map(c -> ResponseEntity.ok(Map.of("name", c.getName())))
                .orElse(ResponseEntity.notFound().build());
    }
}
