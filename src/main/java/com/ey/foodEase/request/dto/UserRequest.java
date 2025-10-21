package com.ey.foodEase.request.dto;

import java.util.Set;

import com.ey.foodEase.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    private String username;
    private String password;
    private String phone;
    private Set<Role> roles;
}
