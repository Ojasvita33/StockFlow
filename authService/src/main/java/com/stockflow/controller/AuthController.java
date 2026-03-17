package com.stockflow.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

import com.stockflow.dto.CompanyRegisterRequest;
import com.stockflow.dto.CompanyRegisterResponse;
import com.stockflow.model.User;
import com.stockflow.service.UserService;
import com.stockflow.repository.UserRepository;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService service;

    @Autowired
    private UserRepository userRepository;

    // Company registration - takes companyName + username + email + password
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody CompanyRegisterRequest request) {
        try {
            CompanyRegisterResponse response = service.registerCompany(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        try {
            String username = body.get("username");
            String password = body.get("password");

            String token = service.login(username, password);
            User user = service.getUser(username);

            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "role", user.getRole(),
                    "companyId", user.getCompanyId() != null ? user.getCompanyId() : 0));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/create-admin")
    public ResponseEntity<?> createAdmin(@RequestBody User user) {
        user.setRole("ROLE_ADMIN");
        user.setPassword(service.encodePassword(user.getPassword()));
        return ResponseEntity.ok(userRepository.save(user));
    }
}
