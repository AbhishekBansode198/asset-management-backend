package com.example.demo.asset_management.controller;
import com.example.demo.asset_management.entity.User;
import com.example.demo.asset_management.dto.LoginRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.demo.asset_management.service.UserService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        String username = request.getUsername();
        String password = request.getPassword();

        // Default Admin
        if ("admin".equals(username) && "admin123".equals(password)) {
            return ResponseEntity.ok("ADMIN");
        }

        // DB User
        User user = userService.findByUsername(username);

        if (user != null && user.getPassword().equals(password)) {
            return ResponseEntity.ok(user.getRole().getRoleName());
        }

        return ResponseEntity.status(401).body("Invalid Username or Password");
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("Logout successful");
    }
}