package com.studenttaskmanager.service;

import com.studenttaskmanager.dto.AuthDTOs;
import com.studenttaskmanager.entity.*;
import com.studenttaskmanager.repository.*;
import com.studenttaskmanager.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Transactional
    public AuthDTOs.AuthResponse register(AuthDTOs.RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);

        // Create default categories for new user
        List<Object[]> defaults = List.of(
            new Object[]{"Math", "#ef4444", "➗"},
            new Object[]{"DBMS", "#3b82f6", "🗄️"},
            new Object[]{"Operating Systems", "#10b981", "💻"},
            new Object[]{"Machine Learning", "#8b5cf6", "🤖"},
            new Object[]{"Personal", "#f59e0b", "👤"}
        );

        for (Object[] d : defaults) {
            Category cat = Category.builder()
                    .name((String) d[0])
                    .color((String) d[1])
                    .icon((String) d[2])
                    .user(user)
                    .build();
            categoryRepository.save(cat);
        }

        String token = jwtUtils.generateTokenFromEmail(user.getEmail());
        return new AuthDTOs.AuthResponse(token, user.getId(), user.getName(), user.getEmail());
    }

    public AuthDTOs.AuthResponse login(AuthDTOs.LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        String token = jwtUtils.generateJwtToken(authentication);
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new AuthDTOs.AuthResponse(token, user.getId(), user.getName(), user.getEmail());
    }
}
