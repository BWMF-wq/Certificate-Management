package com.campus.certificate.service;

import com.campus.certificate.domain.StudentUser;
import com.campus.certificate.dto.UserDtos;
import com.campus.certificate.exception.BusinessException;
import com.campus.certificate.exception.ResourceNotFoundException;
import com.campus.certificate.repository.StudentUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final StudentUserRepository userRepository;

    @Transactional(readOnly = true)
    public UserDtos.UserResponse get(Long userId) {
        return UserDtos.UserResponse.from(find(userId));
    }

    @Transactional
    public UserDtos.UserResponse update(Long userId, UserDtos.UpdateProfileRequest request) {
        StudentUser user = find(userId);
        String email = request.email().trim().toLowerCase();
        userRepository.findByEmailIgnoreCase(email)
                .filter(existing -> !existing.getId().equals(userId))
                .ifPresent(existing -> { throw new BusinessException(HttpStatus.CONFLICT, "该邮箱已被使用"); });
        user.setName(request.name().trim());
        user.setEmail(email);
        user.setSchool(clean(request.school()));
        user.setMajor(clean(request.major()));
        user.setGraduationYear(request.graduationYear());
        return UserDtos.UserResponse.from(userRepository.save(user));
    }

    private StudentUser find(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("用户不存在"));
    }

    private String clean(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
