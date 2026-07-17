package com.campus.certificate.service;

import com.campus.certificate.domain.*;
import com.campus.certificate.dto.CertificateDtos;
import com.campus.certificate.exception.BusinessException;
import com.campus.certificate.exception.ResourceNotFoundException;
import com.campus.certificate.repository.CertificateRepository;
import com.campus.certificate.repository.StudentUserRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CertificateService {
    private final CertificateRepository certificateRepository;
    private final StudentUserRepository userRepository;
    private final FileStorageService storageService;

    public record Attachment(FileSystemResource resource, String fileName, String contentType) {}

    @Transactional(readOnly = true)
    public CertificateDtos.PageResponse<CertificateDtos.CertificateResponse> list(
            Long userId, String keyword, CertificateCategory category, CertificateLevel level, AwardType awardType,
            int page, int size, String sort
    ) {
        int safePage = Math.max(0, page);
        int safeSize = Math.min(Math.max(size, 1), 50);
        Sort sorting = parseSort(sort);
        Specification<Certificate> specification = buildSpecification(userId, keyword, category, level, awardType);
        Page<Certificate> result = certificateRepository.findAll(specification, PageRequest.of(safePage, safeSize, sorting));
        List<CertificateDtos.CertificateResponse> content = result.getContent().stream()
                .map(CertificateDtos.CertificateResponse::from).toList();
        return new CertificateDtos.PageResponse<>(content, result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages());
    }

    @Transactional(readOnly = true)
    public CertificateDtos.CertificateResponse get(Long userId, Long certificateId) {
        return CertificateDtos.CertificateResponse.from(findOwned(userId, certificateId));
    }

    @Transactional
    public CertificateDtos.CertificateResponse create(Long userId, CertificateDtos.UpsertCertificateRequest request) {
        validate(request);
        StudentUser user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("用户不存在"));
        Certificate certificate = new Certificate();
        certificate.setUser(user);
        apply(certificate, request);
        return CertificateDtos.CertificateResponse.from(certificateRepository.save(certificate));
    }

    @Transactional
    public CertificateDtos.CertificateResponse update(Long userId, Long certificateId, CertificateDtos.UpsertCertificateRequest request) {
        validate(request);
        Certificate certificate = findOwned(userId, certificateId);
        apply(certificate, request);
        return CertificateDtos.CertificateResponse.from(certificateRepository.save(certificate));
    }

    @Transactional
    public void delete(Long userId, Long certificateId) {
        Certificate certificate = findOwned(userId, certificateId);
        String storedName = certificate.getStoredFileName();
        certificateRepository.delete(certificate);
        storageService.delete(storedName);
    }

    @Transactional
    public CertificateDtos.CertificateResponse upload(Long userId, Long certificateId, MultipartFile file) {
        Certificate certificate = findOwned(userId, certificateId);
        FileStorageService.StoredFile stored = storageService.store(userId, file);
        String oldStoredName = certificate.getStoredFileName();
        certificate.setFileName(stored.originalName());
        certificate.setStoredFileName(stored.storedName());
        certificate.setFileContentType(stored.contentType());
        certificate.setFileSize(stored.size());
        Certificate saved = certificateRepository.save(certificate);
        storageService.delete(oldStoredName);
        return CertificateDtos.CertificateResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public Attachment download(Long userId, Long certificateId) {
        Certificate certificate = findOwned(userId, certificateId);
        if (certificate.getStoredFileName() == null) {
            throw new ResourceNotFoundException("该荣誉证书没有附件");
        }
        Path path = storageService.load(certificate.getStoredFileName());
        return new Attachment(new FileSystemResource(path), certificate.getFileName(), certificate.getFileContentType());
    }

    @Transactional
    public CertificateDtos.CertificateResponse deleteAttachment(Long userId, Long certificateId) {
        Certificate certificate = findOwned(userId, certificateId);
        String storedName = certificate.getStoredFileName();
        certificate.setFileName(null);
        certificate.setStoredFileName(null);
        certificate.setFileContentType(null);
        certificate.setFileSize(null);
        Certificate saved = certificateRepository.save(certificate);
        storageService.delete(storedName);
        return CertificateDtos.CertificateResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public CertificateDtos.DashboardResponse dashboard(Long userId) {
        List<Certificate> certificates = certificateRepository.findAllByUserIdOrderByIssueDateDesc(userId);
        int currentYear = LocalDate.now().getYear();
        long thisYear = certificates.stream().filter(c -> c.getIssueDate().getYear() == currentYear).count();
        long withAttachment = certificates.stream().filter(c -> c.getStoredFileName() != null).count();
        long issuerCount = certificates.stream().map(Certificate::getIssuer).filter(value -> value != null && !value.isBlank())
                .map(value -> value.trim().toLowerCase()).distinct().count();

        List<CertificateDtos.CategoryStat> categories = new ArrayList<>();
        for (CertificateCategory category : CertificateCategory.values()) {
            long count = certificates.stream().filter(c -> c.getCategory() == category).count();
            if (count > 0) categories.add(new CertificateDtos.CategoryStat(category, count));
        }
        categories.sort(Comparator.comparingLong(CertificateDtos.CategoryStat::count).reversed());

        List<CertificateDtos.LevelStat> levels = new ArrayList<>();
        for (CertificateLevel level : CertificateLevel.values()) {
            long count = certificates.stream().filter(c -> c.getLevel() == level).count();
            if (count > 0) levels.add(new CertificateDtos.LevelStat(level, count));
        }
        levels.sort(Comparator.comparingLong(CertificateDtos.LevelStat::count).reversed());

        List<CertificateDtos.AwardTypeStat> awardTypes = new ArrayList<>();
        for (AwardType awardType : AwardType.values()) {
            long count = certificates.stream().filter(c -> c.getAwardType() == awardType).count();
            if (count > 0) awardTypes.add(new CertificateDtos.AwardTypeStat(awardType, count));
        }
        awardTypes.sort(Comparator.comparingLong(CertificateDtos.AwardTypeStat::count).reversed());

        YearMonth now = YearMonth.now();
        List<CertificateDtos.MonthlyStat> trend = new ArrayList<>();
        for (int i = 11; i >= 0; i--) {
            YearMonth month = now.minusMonths(i);
            long count = certificates.stream().filter(c -> YearMonth.from(c.getIssueDate()).equals(month)).count();
            trend.add(new CertificateDtos.MonthlyStat(month.format(DateTimeFormatter.ofPattern("yyyy-MM")), count));
        }
        List<CertificateDtos.CertificateResponse> recent = certificates.stream().limit(5)
                .map(CertificateDtos.CertificateResponse::from).toList();
        return new CertificateDtos.DashboardResponse(certificates.size(), thisYear, withAttachment, issuerCount,
                categories, levels, awardTypes, trend, recent);
    }

    private Specification<Certificate> buildSpecification(Long userId, String keyword, CertificateCategory category,
                                                          CertificateLevel level, AwardType awardType) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("user").get("id"), userId));
            if (keyword != null && !keyword.isBlank()) {
                String like = "%" + keyword.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), like),
                        cb.like(cb.lower(root.get("issuer")), like),
                        cb.like(cb.lower(root.get("credentialNo")), like)
                ));
            }
            if (category != null) predicates.add(cb.equal(root.get("category"), category));
            if (level != null) predicates.add(cb.equal(root.get("level"), level));
            if (awardType != null) predicates.add(cb.equal(root.get("awardType"), awardType));
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

    private Sort parseSort(String sort) {
        String value = sort == null ? "issueDate,desc" : sort;
        String[] parts = value.split(",", 2);
        String property = switch (parts[0]) {
            case "name", "issuer", "issueDate", "expiryDate", "createdAt" -> parts[0];
            default -> "issueDate";
        };
        Sort.Direction direction = parts.length > 1 && "asc".equalsIgnoreCase(parts[1]) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return Sort.by(direction, property);
    }

    private Certificate findOwned(Long userId, Long certificateId) {
        return certificateRepository.findByIdAndUserId(certificateId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("荣誉证书不存在"));
    }

    private void validate(CertificateDtos.UpsertCertificateRequest request) {
        if (request.issueDate().isAfter(LocalDate.now())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "取得日期不能晚于今天");
        }
        if (request.expiryDate() != null && request.expiryDate().isBefore(request.issueDate())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "到期日期不能早于取得日期");
        }
        if (request.credentialUrl() != null && !request.credentialUrl().isBlank()) {
            try {
                URI uri = URI.create(request.credentialUrl().trim());
                if (!("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme()))) throw new IllegalArgumentException();
            } catch (IllegalArgumentException ex) {
                throw new BusinessException(HttpStatus.BAD_REQUEST, "验证地址必须是有效的 http/https 链接");
            }
        }
    }

    private void apply(Certificate certificate, CertificateDtos.UpsertCertificateRequest request) {
        certificate.setName(request.name().trim());
        certificate.setIssuer(request.issuer().trim());
        certificate.setCategory(request.category());
        certificate.setLevel(request.level());
        certificate.setAwardType(request.awardType());
        certificate.setIssueDate(request.issueDate());
        certificate.setExpiryDate(request.expiryDate());
        certificate.setCredentialNo(clean(request.credentialNo()));
        certificate.setCredentialUrl(clean(request.credentialUrl()));
        certificate.setDescription(clean(request.description()));
    }

    private String clean(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}

