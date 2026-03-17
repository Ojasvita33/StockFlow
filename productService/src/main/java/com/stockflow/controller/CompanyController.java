package com.stockflow.controller;

import com.stockflow.repository.CompanyRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/company")
public class CompanyController {

    @Autowired
    private CompanyRepository companyRepository;

    @GetMapping("/name")
    public ResponseEntity<?> getCompanyName(HttpServletRequest req) {
        Object companyId = req.getAttribute("companyId");
        if (companyId == null) return ResponseEntity.ok(Map.of("name", ""));
        return companyRepository.findById((Long) companyId)
                .map(c -> ResponseEntity.ok(Map.of("name", c.getName())))
                .orElse(ResponseEntity.ok(Map.of("name", "")));
    }
}