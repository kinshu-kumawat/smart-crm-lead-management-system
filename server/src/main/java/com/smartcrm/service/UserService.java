package com.smartcrm.service;

import com.smartcrm.dto.auth.RegisterRequest;
import com.smartcrm.dto.auth.UserDto;
import com.smartcrm.entity.User;
import com.smartcrm.entity.enums.Role;
import com.smartcrm.exception.BadRequestException;
import com.smartcrm.exception.ResourceNotFoundException;
import com.smartcrm.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserDto::new)
                .collect(Collectors.toList());
    }

    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return new UserDto(user);
    }

    @Transactional
    public UserDto createUser(RegisterRequest request) {
        String email = request.getEmail().toLowerCase().trim();
        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("Email already in use");
        }

        Role role = request.getRole() != null ? request.getRole() : Role.SALES_REP;
        User user = new User(email, passwordEncoder.encode(request.getPassword()), request.getFullName().trim(), role);
        user = userRepository.save(user);

        return new UserDto(user);
    }

    @Transactional
    public UserDto toggleUserStatus(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        user.setActive(!user.isActive());
        user = userRepository.save(user);
        return new UserDto(user);
    }
}
