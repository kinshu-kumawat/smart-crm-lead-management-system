package com.smartcrm.service;

import com.smartcrm.config.JwtTokenProvider;
import com.smartcrm.dto.auth.AuthResponse;
import com.smartcrm.dto.auth.LoginRequest;
import com.smartcrm.dto.auth.RegisterRequest;
import com.smartcrm.dto.auth.UserDto;
import com.smartcrm.entity.User;
import com.smartcrm.entity.enums.Role;
import com.smartcrm.exception.BadRequestException;
import com.smartcrm.exception.UnauthorizedException;
import com.smartcrm.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        if (!user.isActive()) {
            throw new UnauthorizedException("Your account is deactivated. Please contact an administrator.");
        }

        String token = tokenProvider.generateToken(user);
        return new AuthResponse(token, new UserDto(user));
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = request.getEmail().toLowerCase().trim();
        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("An account with this email already exists");
        }

        Role role = request.getRole() != null ? request.getRole() : Role.SALES_REP;
        User user = new User(email, passwordEncoder.encode(request.getPassword()), request.getFullName().trim(), role);
        user = userRepository.save(user);

        String token = tokenProvider.generateToken(user);
        return new AuthResponse(token, new UserDto(user));
    }

    public UserDto getCurrentUser(User user) {
        return new UserDto(user);
    }
}
