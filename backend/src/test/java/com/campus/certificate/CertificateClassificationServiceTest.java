package com.campus.certificate;

import com.campus.certificate.domain.CertificateCategory;
import com.campus.certificate.domain.CertificateLevel;
import com.campus.certificate.service.CertificateClassificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class CertificateClassificationServiceTest {
    private final CertificateClassificationService service = new CertificateClassificationService();

    @ParameterizedTest
    @MethodSource("knownCertificates")
    void classifiesKnownCertificates(String name, String issuer,
                                     CertificateCategory expectedCategory, CertificateLevel expectedLevel) {
        var result = service.suggest(name, issuer);

        assertThat(result.category()).isEqualTo(expectedCategory);
        assertThat(result.level()).isEqualTo(expectedLevel);
        assertThat(result.confidence()).isGreaterThanOrEqualTo(66);
    }

    @Test
    void fallsBackForUnknownCertificate() {
        var result = service.suggest("自定义收藏证明", "个人收藏");

        assertThat(result.category()).isEqualTo(CertificateCategory.OTHER);
        assertThat(result.level()).isEqualTo(CertificateLevel.OTHER);
        assertThat(result.fallback()).isTrue();
    }

    private static Stream<Arguments> knownCertificates() {
        return Stream.of(
                Arguments.of("全国大学英语四级考试成绩报告单", "教育部教育考试院",
                        CertificateCategory.LANGUAGE_EXAM, CertificateLevel.NATIONAL),
                Arguments.of("中华人民共和国教师资格证书", "教育部",
                        CertificateCategory.PROFESSIONAL_QUALIFICATION, CertificateLevel.NATIONAL),
                Arguments.of("AWS Certified Solutions Architect", "Amazon Web Services",
                        CertificateCategory.SKILL_CERTIFICATION, CertificateLevel.INTERNATIONAL),
                Arguments.of("工学学士学位证书", "河南农业大学",
                        CertificateCategory.ACADEMIC_EDUCATION, CertificateLevel.UNIVERSITY),
                Arguments.of("河南省大学生数学建模竞赛一等奖", "河南省教育厅",
                        CertificateCategory.COMPETITION_AWARD, CertificateLevel.PROVINCIAL),
                Arguments.of("Java 工程师培训结业证书", "职业培训中心",
                        CertificateCategory.TRAINING_CERTIFICATE, CertificateLevel.INSTITUTION),
                Arguments.of("计算机软件著作权登记证书", "国家版权局",
                        CertificateCategory.INTELLECTUAL_PROPERTY, CertificateLevel.NATIONAL),
                Arguments.of("优秀志愿者服务证书", "河南农业大学",
                        CertificateCategory.VOLUNTEER_SERVICE, CertificateLevel.UNIVERSITY)
        );
    }
}
