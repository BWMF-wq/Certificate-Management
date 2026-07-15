package com.campus.certificate.repository;

import com.campus.certificate.domain.StudentUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentUserRepository extends JpaRepository<StudentUser, Long> {
    Optional<StudentUser> findByEmailIgnoreCase(String email);
    Optional<StudentUser> findByStudentId(String studentId);
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByStudentId(String studentId);
}

