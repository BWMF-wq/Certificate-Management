package com.campus.certificate.service;

import com.campus.certificate.domain.StudentUser;
import com.campus.certificate.dto.AuthDtos;
import com.campus.certificate.dto.UserDtos;
import com.campus.certificate.exception.BusinessException;
import com.campus.certificate.repository.StudentUserRepository;
import com.campus.certificate.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final StudentUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public AuthDtos.AuthResponse register(AuthDtos.RegisterRequest request) {
        String email = request.email().trim().toLowerCase();
        String studentId = request.studentId().trim();
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new BusinessException(HttpStatus.CONFLICT, "该邮箱已注册");
        }
        if (userRepository.existsByStudentId(studentId)) {
            throw new BusinessException(HttpStatus.CONFLICT, "该学号已注册");
        }
        StudentUser user = new StudentUser();
        user.setName(request.name().trim());
        user.setStudentId(studentId);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user = userRepository.save(user);
        return new AuthDtos.AuthResponse(jwtService.generate(user.getId()), UserDtos.UserResponse.from(user));
    }

    @Transactional(readOnly = true)
    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest request) {
        String account = request.account().trim();
        StudentUser user = account.contains("@")
                ? userRepository.findByEmailIgnoreCase(account).orElseThrow(this::invalidCredentials)
                : userRepository.findByStudentId(account).orElseThrow(this::invalidCredentials);
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw invalidCredentials();
        }
        return new AuthDtos.AuthResponse(jwtService.generate(user.getId()), UserDtos.UserResponse.from(user));
    }

    private BusinessException invalidCredentials() {
        return new BusinessException(HttpStatus.UNAUTHORIZED, "账号或密码不正确");
    }
}

