package com.app.gighub.controllers;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.gighub.models.User;
import com.app.gighub.services.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {
        Map<String, String> response = new HashMap<>();
        try {
            // 1. Verify the email and password
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );
            
            // 2. Set the secure session in Spring
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            response.put("message", "Login successful!");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            // 3. Catch bad passwords or missing users
            response.put("error", "Invalid email or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        Map<String, String> response = new HashMap<>();
        
        // 1. Check if user already exists
        if (userService.getByEmail(user.getEmail()) != null) {
            response.put("error", "A user with this email already exists.");
            return ResponseEntity.badRequest().body(response);
        }

        // 2. Save the user (Your UserService automatically hashes the password!)
        userService.save(user);
        
        response.put("message", "Registration successful!");
        return ResponseEntity.ok(response);
    }
}