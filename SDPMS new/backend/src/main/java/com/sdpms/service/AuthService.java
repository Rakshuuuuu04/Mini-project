package com.sdpms.service;

import com.sdpms.dto.AuthResponse;
import com.sdpms.dto.LoginRequest;
import com.sdpms.model.*;
import com.sdpms.security.JwtUtil;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserService userService, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse login(LoginRequest request) {
        User user = userService.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));
        if (!user.isActive()) throw new BadCredentialsException("Account is deactivated");
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash()))
            throw new BadCredentialsException("Invalid username or password");

        userService.updateLastLogin(user.getUsername());

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name(), user.getId());
        AuthResponse response = new AuthResponse(token, user.getUsername(), user.getRole(), user.getId());
        response.setFullName(getFullName(user));
        if (user.getRole() == Role.FACULTY) {
            Faculty f = userService.findFacultyByUsername(user.getUsername()).orElse(null);
            if (f != null && f.getDepartment() != null) {
                response.setDepartmentId(f.getDepartment().getId());
                response.setDepartmentName(f.getDepartment().getName());
            }
        }
        if (user.getRole() == Role.STUDENT) {
            Student s = userService.findStudentByUsername(user.getUsername()).orElse(null);
            if (s != null && s.getDepartment() != null) {
                response.setDepartmentId(s.getDepartment().getId());
                response.setDepartmentName(s.getDepartment().getName());
            }
        }
        return response;
    }

    private String getFullName(User user) {
        if (user instanceof Student s) return s.getFullName();
        if (user instanceof Faculty f) return f.getFullName();
        return user.getUsername();
    }
}
