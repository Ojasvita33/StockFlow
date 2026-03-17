package com.stockflow.service;

import com.stockflow.dto.CompanyRegisterRequest;
import com.stockflow.dto.CompanyRegisterResponse;
import com.stockflow.model.Company;
import com.stockflow.model.User;
import com.stockflow.repository.CompanyRepository;
import com.stockflow.repository.UserRepository;
import com.stockflow.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository repo;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public User register(User user) {
        if (repo.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (repo.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null) {
            user.setRole("ROLE_ADMIN");
        }
        return repo.save(user);
    }

    @Override
    public CompanyRegisterResponse registerCompany(CompanyRegisterRequest request) {

        if (repo.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (repo.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // 1. Create and save the Company
        Company company = new Company();
        company.setName(request.getCompanyName());
        company.setEmail(request.getEmail());
        Company savedCompany = companyRepository.save(company);

        // 2. Create the User linked to the company
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("ROLE_ADMIN");
        user.setCompanyId(savedCompany.getId());
        User savedUser = repo.save(user);

        return new CompanyRegisterResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedCompany.getId(),
                savedCompany.getName(),
                savedUser.getRole()
        );
    }

    @Override
    public String login(String username, String password) {
        User user = repo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (passwordEncoder.matches(password, user.getPassword())) {
            return jwtUtil.generateToken(username, user.getRole(), user.getCompanyId());
        }
        throw new RuntimeException("Invalid credentials");
    }

    @Override
    public User getUser(String username) {
        return repo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
}
