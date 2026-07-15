package com.campus.certificate.repository;

import com.campus.certificate.domain.Certificate;
import com.campus.certificate.domain.CertificateCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface CertificateRepository extends JpaRepository<Certificate, Long>, JpaSpecificationExecutor<Certificate> {
    Optional<Certificate> findByIdAndUserId(Long id, Long userId);
    List<Certificate> findAllByUserIdOrderByIssueDateDesc(Long userId);
    long countByUserId(Long userId);
    long countByUserIdAndCategory(Long userId, CertificateCategory category);
}
