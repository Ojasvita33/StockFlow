package com.stockflow.service;

import com.stockflow.dto.CompanyRegisterRequest;
import com.stockflow.dto.CompanyRegisterResponse;
import com.stockflow.model.User;

public interface UserService {
    User register(User user);
    CompanyRegisterResponse registerCompany(CompanyRegisterRequest request);
    String login(String username, String password);
    User getUser(String username);
    String encodePassword(String rawPassword);
}
