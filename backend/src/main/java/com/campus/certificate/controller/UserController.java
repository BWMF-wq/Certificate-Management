package com.campus.certificate.controller;

import com.campus.certificate.dto.UserDtos;
import com.campus.certificate.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/me")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public UserDtos.UserResponse get(@AuthenticationPrincipal Long userId) {
        return userService.get(userId);
    }

    @PutMapping
    public UserDtos.UserResponse update(@AuthenticationPrincipal Long userId, @Valid @RequestBody UserDtos.UpdateProfileRequest request) {
        return userService.update(userId, request);
    }
}

