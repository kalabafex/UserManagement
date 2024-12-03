package com.kai.Registration.controller;

import com.kai.Registration.entity.LoginRequest;
import com.kai.Registration.entity.LoginResponse;
import com.kai.Registration.entity.User;
import com.kai.Registration.service.AuthService;
import com.kai.Registration.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {
    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @PostMapping("/api/auth/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        userService.registerUser(user);
        return ResponseEntity.ok("User registered successfully!");
    }
    @PostMapping("/api/auth/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            String token = authService.login(loginRequest.getEmail(), loginRequest.getPassword());
            return ResponseEntity.ok(new LoginResponse(token));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }
    @GetMapping("/user/get")
    @PreAuthorize("hasAuthority('USER')") // Allow only users with ROLE_USER
    public String userAccess() {
        return "User Access Granted";
    }

    @GetMapping("/admin/get")
    @PreAuthorize("hasAuthority('ADMIN')") // Allow only users with ROLE_ADMIN
    public String adminAccess() {
        return "Admin Access Granted";
    }
}

