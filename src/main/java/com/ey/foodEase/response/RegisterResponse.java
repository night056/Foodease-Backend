package com.ey.foodEase.response;

import java.util.Set;

public class RegisterResponse {
    private Long id;
    private String username;
    private String phone;
    private Set<String> roles;

    public RegisterResponse() {}

    public RegisterResponse(Long id, String username, String phone, Set<String> roles) {
        this.id = id;
        this.username = username;
        this.phone = phone;
        this.roles = roles;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

   
}