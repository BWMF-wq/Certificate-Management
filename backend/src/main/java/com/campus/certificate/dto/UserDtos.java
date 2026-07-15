package com.campus.certificate.dto;

import com.campus.certificate.domain.StudentUser;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public final class UserDtos {
    private UserDtos() {}

    public record UserResponse(
            Long id,
            String name,
            String studentId,
            String email,
            String school,
            String major,
            Integer graduationYear,
            LocalDateTime createdAt
    ) {
        public static UserResponse from(StudentUser user) {
            return new UserResponse(user.getId(), user.getName(), user.getStudentId(), user.getEmail(),
                    user.getSchool(), user.getMajor(), user.getGraduationYear(), user.getCreatedAt());
        }
    }

    public record UpdateProfileRequest(
            @NotBlank(message = "姓名不能为空") @Size(max = 50, message = "姓名不能超过50个字符") String name,
            @NotBlank(message = "邮箱不能为空") @Email(message = "邮箱格式不正确") String email,
            @Size(max = 100, message = "学校不能超过100个字符") String school,
            @Size(max = 100, message = "专业不能超过100个字符") String major,
            @Min(value = 2000, message = "毕业年份不正确") @Max(value = 2100, message = "毕业年份不正确") Integer graduationYear
    ) {}
}

