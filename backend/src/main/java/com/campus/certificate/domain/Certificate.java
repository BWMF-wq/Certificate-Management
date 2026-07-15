package com.campus.certificate.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "certificates")
@Getter
@Setter
@NoArgsConstructor
public class Certificate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private StudentUser user;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 120)
    private String issuer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CertificateCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CertificateLevel level;

    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "credential_no", length = 100)
    private String credentialNo;

    @Column(name = "credential_url", length = 500)
    private String credentialUrl;

    @Column(length = 1000)
    private String description;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "stored_file_name")
    private String storedFileName;

    @Column(name = "file_content_type", length = 100)
    private String fileContentType;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Transient
    public CertificateStatus getStatus() {
        if (expiryDate == null) return CertificateStatus.PERMANENT;
        LocalDate today = LocalDate.now();
        if (expiryDate.isBefore(today)) return CertificateStatus.EXPIRED;
        if (!expiryDate.isAfter(today.plusDays(90))) return CertificateStatus.EXPIRING;
        return CertificateStatus.VALID;
    }

    @PrePersist
    void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

