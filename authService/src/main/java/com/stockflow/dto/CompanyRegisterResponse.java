package com.stockflow.dto;

public class CompanyRegisterResponse {

    private Long userId;
    private String username;
    private String email;
    private Long companyId;
    private String companyName;
    private String role;

    public CompanyRegisterResponse(Long userId, String username, String email,
                                   Long companyId, String companyName, String role) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.companyId = companyId;
        this.companyName = companyName;
        this.role = role;
    }

    public Long getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public Long getCompanyId() { return companyId; }
    public String getCompanyName() { return companyName; }
    public String getRole() { return role; }
}
