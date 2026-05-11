package com.prueba.auth_service.service;

import com.prueba.auth_service.dto.AuthResponse;
import com.prueba.auth_service.dto.UserLoginRequest;
import com.prueba.auth_service.dto.UserRegisterRequest;
import com.prueba.auth_service.dto.UserResponse;
import com.prueba.auth_service.entity.Role;
import com.prueba.auth_service.entity.User;
import com.prueba.auth_service.mapper.UserMapper;
import com.prueba.auth_service.repository.RoleRepository;
import com.prueba.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserMapper userMapper;

    @Transactional
    public AuthResponse register(UserRegisterRequest request) {
        log.info("Registering new user: {}", request.getUsername());

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + request.getEmail());
        }

        Role userRole = roleRepository.findByName("ROLE_USER")
            .orElseThrow(() -> new RuntimeException("Default role ROLE_USER not found in database"));

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRoles(new HashSet<>(Set.of(userRole)));

        user = userRepository.save(user);
        log.info("User registered successfully: {}", user.getUsername());

        return buildAuthResponse(user);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(UserLoginRequest request) {
        log.info("Login attempt for user: {}", request.getUsername());

        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (AuthenticationException ex) {
            log.warn("Authentication failed for user: {}", request.getUsername());
            throw new IllegalArgumentException("Invalid username or password");
        }

        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + request.getUsername()));

        log.info("User logged in successfully: {}", user.getUsername());
        return buildAuthResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserByUsername(String username) {
        return userRepository.findByUsername(username)
            .map(userMapper::toUserResponse)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    private AuthResponse buildAuthResponse(User user) {
        Set<GrantedAuthority> authorities = user.getRoles().stream()
            .map(role -> (GrantedAuthority) role::getName)
            .collect(Collectors.toSet());

        String token = jwtTokenProvider.generateTokenFromUsername(user.getUsername(), authorities);

        return AuthResponse.builder()
            .token(token)
            .type("Bearer")
            .user(userMapper.toUserResponse(user))
            .expiresIn(jwtTokenProvider.getJwtExpirationMs())
            .build();
    }
}
