package com.campus.certificate.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public final class AuthDtos {
    private AuthDtos() {}

    public record RegisterRequest(
            @NotBlank(message = "姓名不能为空") @Size(max = 50, message = "姓名不能超过50个字符") String name,
            @NotBlank(message = "学号不能为空") @Pattern(regexp = "^[A-Za-z0-9_-]{4,30}$", message = "学号格式不正确") String studentId,
            @NotBlank(message = "邮箱不能为空") @Email(message = "邮箱格式不正确") String email,
            @NotBlank(message = "密码不能为空") @Size(min = 8, max = 64, message = "密码长度应为8-64位") String password
    ) {}

    public record LoginRequest(
            @NotBlank(message = "账号不能为空") String account,
            @NotBlank(message = "密码不能为空") String password
    ) {}

    public record AuthResponse(String token, UserDtos.UserResponse user) {}
}

