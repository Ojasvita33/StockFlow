package com.stockflow.service;

import com.stockflow.model.User;

public interface UserService {
    User register(User user);
    String login(String username, String password);
}
