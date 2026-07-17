package com.campus.certificate.repository;

import com.campus.certificate.domain.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface CertificateRepository extends JpaRepository<Certificate, Long>, JpaSpecificationExecutor<Certificate> {
    Optional<Certificate> findByIdAndUserIdAndDeletedAtIsNull(Long id, Long userId);
    Optional<Certificate> findByIdAndUserIdAndDeletedAtIsNotNull(Long id, Long userId);
    List<Certificate> findAllByUserIdAndDeletedAtIsNullOrderByIssueDateDesc(Long userId);
    long countByUserIdAndDeletedAtIsNotNull(Long userId);
}
