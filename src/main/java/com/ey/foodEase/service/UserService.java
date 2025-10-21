package com.ey.foodEase.service;

import com.ey.foodEase.model.Role;
import com.ey.foodEase.model.User;
import com.ey.foodEase.repository.UserRepository;
import com.ey.foodEase.response.dto.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> getUserByPhone(String phone) {
        return userRepository.findByPhone(phone);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Set<Role> getUserRoles(String username) {
        return userRepository.findByUsername(username)
                .map(User::getRoles)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User updateUserRoles(Long userId, Set<Role> roles) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRoles(roles);
        return userRepository.save(user);
    }

    public Set<User> getUsersByRole(Role role) {
        return userRepository.findAll().stream()
                .filter(user -> user.getRoles().contains(role))
                .collect(Collectors.toSet());
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getPhone(),
                        user.getRoles().stream().map(Enum::name).collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
    }
}