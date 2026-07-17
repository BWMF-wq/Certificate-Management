package com.campus.certificate.dto;

import com.campus.certificate.domain.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public final class CertificateDtos {
    private CertificateDtos() {}

    public record UpsertCertificateRequest(
            @NotBlank(message = "证书名称不能为空") @Size(max = 120, message = "证书名称不能超过120个字符") String name,
            @NotBlank(message = "颁发机构不能为空") @Size(max = 120, message = "颁发机构不能超过120个字符") String issuer,
            @NotNull(message = "请选择证书分类") CertificateCategory category,
            @NotNull(message = "请选择荣誉级别") CertificateLevel level,
            @NotNull(message = "请选择奖项分类") AwardType awardType,
            @NotNull(message = "请选择取得日期") LocalDate issueDate,
            LocalDate expiryDate,
            @Size(max = 100, message = "证书编号不能超过100个字符") String credentialNo,
            @Size(max = 500, message = "验证地址不能超过500个字符") String credentialUrl,
            @Size(max = 1000, message = "备注不能超过1000个字符") String description
    ) {}

    public record CertificateResponse(
            Long id,
            String name,
            String issuer,
            CertificateCategory category,
            CertificateLevel level,
            AwardType awardType,
            LocalDate issueDate,
            LocalDate expiryDate,
            String credentialNo,
            String credentialUrl,
            String description,
            String fileName,
            String fileContentType,
            Long fileSize,
            boolean hasAttachment,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        public static CertificateResponse from(Certificate certificate) {
            return new CertificateResponse(
                    certificate.getId(), certificate.getName(), certificate.getIssuer(), certificate.getCategory(),
                    certificate.getLevel(), certificate.getAwardType(), certificate.getIssueDate(), certificate.getExpiryDate(),
                    certificate.getCredentialNo(), certificate.getCredentialUrl(), certificate.getDescription(),
                    certificate.getFileName(), certificate.getFileContentType(), certificate.getFileSize(),
                    certificate.getStoredFileName() != null, certificate.getCreatedAt(), certificate.getUpdatedAt()
            );
        }
    }

    public record PageResponse<T>(List<T> content, int page, int size, long totalElements, int totalPages) {}

    public record CategoryStat(CertificateCategory category, long count) {}
    public record LevelStat(CertificateLevel level, long count) {}
    public record AwardTypeStat(AwardType awardType, long count) {}
    public record MonthlyStat(String month, long count) {}
    public record DashboardResponse(
            long total,
            long thisYear,
            long withAttachment,
            long issuerCount,
            List<CategoryStat> categories,
            List<LevelStat> levels,
            List<AwardTypeStat> awardTypes,
            List<MonthlyStat> monthlyTrend,
            List<CertificateResponse> recent
    ) {}
}

