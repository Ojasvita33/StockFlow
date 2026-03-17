package com.stockflow.controller;

import com.stockflow.model.User;
import com.stockflow.repository.UserRepository;
import com.stockflow.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/users")
public class UserManagementController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    private Long getCompanyId(HttpServletRequest req) {
        Object id = req.getAttribute("companyId");
        if (id == null) throw new RuntimeException("Company context missing");
        return (Long) id;
    }

    // List only users belonging to the admin's company
    @GetMapping
    public List<User> getUsers(HttpServletRequest req) {
        return userRepository.findByCompanyId(getCompanyId(req));
    }

    // Create user under the admin's company
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody Map<String, String> body,
                                        HttpServletRequest req) {
        try {
            Long companyId = getCompanyId(req);

            if (userRepository.existsByUsername(body.get("username"))) {
                return ResponseEntity.badRequest().body("Username already exists");
            }
            if (userRepository.existsByEmail(body.get("email"))) {
                return ResponseEntity.badRequest().body("Email already exists");
            }

            User user = new User();
            user.setUsername(body.get("username"));
            user.setEmail(body.get("email"));
            user.setPassword(userService.encodePassword(body.get("password")));
            user.setRole(body.getOrDefault("role", "ROLE_USER"));
            user.setCompanyId(companyId);  // bind to admin's company

            return ResponseEntity.ok(userRepository.save(user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Delete — only if user belongs to admin's company
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, HttpServletRequest req) {
        Long companyId = getCompanyId(req);
        if (!userRepository.existsByIdAndCompanyId(id, companyId)) {
            return ResponseEntity.status(403).body("Access denied or user not found");
        }
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Change role — only within admin's company
    @PatchMapping("/{id}/role")
    public ResponseEntity<?> updateRole(@PathVariable Long id,
                                        @RequestBody Map<String, String> body,
                                        HttpServletRequest req) {
        Long companyId = getCompanyId(req);
        return userRepository.findById(id)
                .filter(u -> companyId.equals(u.getCompanyId()))
                .map(u -> {
                    u.setRole(body.get("role"));
                    return ResponseEntity.ok(userRepository.save(u));
                })
                .orElse(ResponseEntity.status(403).build());
    }
}
