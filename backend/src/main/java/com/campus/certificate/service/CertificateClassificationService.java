package com.campus.certificate.service;

import com.campus.certificate.domain.CertificateCategory;
import com.campus.certificate.domain.CertificateLevel;
import com.campus.certificate.dto.CertificateDtos;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class CertificateClassificationService {
    private record CategoryRule(CertificateCategory category, List<String> keywords) {}
    private record LevelRule(CertificateLevel level, List<String> keywords) {}
    private record Match<T>(T value, List<String> keywords) {}

    private static final List<CategoryRule> CATEGORY_RULES = List.of(
            category(CertificateCategory.LANGUAGE_EXAM,
                    "大学英语四级", "大学英语六级", "英语四级", "英语六级", "cet-4", "cet4", "cet-6", "cet6",
                    "雅思", "ielts", "托福", "toefl", "托业", "toeic", "日语能力", "jlpt", "韩语能力", "topik",
                    "普通话水平测试", "普通话证书"),
            category(CertificateCategory.ACADEMIC_EDUCATION,
                    "毕业证书", "毕业证", "学位证书", "学位证", "学历证书", "肄业证书"),
            category(CertificateCategory.PROFESSIONAL_QUALIFICATION,
                    "教师资格", "法律职业资格", "注册会计师", "会计专业技术资格", "建造师", "造价工程师",
                    "执业医师", "护士执业", "社会工作者职业资格", "经济专业技术资格", "软考",
                    "计算机技术与软件专业技术资格", "职业资格证书"),
            category(CertificateCategory.SKILL_CERTIFICATION,
                    "全国计算机等级考试", "计算机等级", "ncre", "职业技能等级", "技能等级", "技能认证",
                    "aws certified", "microsoft certified", "cisco certified", "oracle certified", "红帽认证",
                    "hcia", "hcip", "hcie", "阿里云认证", "腾讯云认证", "adobe认证"),
            category(CertificateCategory.INTELLECTUAL_PROPERTY,
                    "专利证书", "发明专利", "实用新型", "外观设计专利", "软件著作权", "作品登记证书", "商标注册证"),
            category(CertificateCategory.INNOVATION_ENTREPRENEURSHIP,
                    "创新创业", "互联网+", "创业训练", "创新训练", "大学生创新项目", "大创", "创业大赛"),
            category(CertificateCategory.COMPETITION_AWARD,
                    "学科竞赛", "竞赛", "大赛", "比赛", "奥林匹克", "数学建模", "挑战杯"),
            category(CertificateCategory.TRAINING_CERTIFICATE,
                    "培训证书", "培训合格", "结业证书", "结业证明", "课程证书", "研修证书", "研修班"),
            category(CertificateCategory.HONORARY_TITLE,
                    "三好学生", "优秀学生", "优秀毕业生", "先进个人", "荣誉称号", "奖学金", "优秀团员", "优秀党员"),
            category(CertificateCategory.VOLUNTEER_SERVICE,
                    "志愿服务", "志愿者", "公益服务"),
            category(CertificateCategory.SOCIAL_PRACTICE,
                    "社会实践", "实习证明", "实践证书"),
            category(CertificateCategory.CULTURE_SPORTS,
                    "文艺", "体育", "运动会", "篮球", "足球", "舞蹈", "合唱", "书法", "摄影")
    );

    private static final List<LevelRule> LEVEL_RULES = List.of(
            level(CertificateLevel.INTERNATIONAL,
                    "国际级", "国际", "ielts", "toefl", "toeic", "jlpt", "topik", "aws certified",
                    "microsoft certified", "cisco certified", "oracle certified", "红帽认证"),
            level(CertificateLevel.NATIONAL,
                    "国家级", "全国", "国家职业资格", "教育部", "国务院", "人力资源和社会保障部", "人社部",
                    "国家知识产权局", "教育部教育考试院", "大学英语四级", "大学英语六级", "英语四级", "英语六级",
                    "cet-4", "cet4", "cet-6", "cet6", "普通话水平测试", "教师资格", "软考", "注册会计师"),
            level(CertificateLevel.PROVINCIAL,
                    "省级", "省教育厅", "省委", "省人民政府", "河北省", "山西省", "辽宁省", "吉林省", "黑龙江省",
                    "江苏省", "浙江省", "安徽省", "福建省", "江西省", "山东省", "河南省", "湖北省", "湖南省",
                    "广东省", "海南省", "四川省", "贵州省", "云南省", "陕西省", "甘肃省", "青海省", "台湾省",
                    "内蒙古自治区", "广西壮族自治区", "西藏自治区", "宁夏回族自治区", "新疆维吾尔自治区",
                    "北京市", "天津市", "上海市", "重庆市", "香港特别行政区", "澳门特别行政区"),
            level(CertificateLevel.MUNICIPAL,
                    "市级", "市教育局", "市委", "市人民政府"),
            level(CertificateLevel.DISTRICT_COUNTY,
                    "区县级", "区级", "县级", "区教育局", "县教育局", "区人民政府", "县人民政府"),
            level(CertificateLevel.UNIVERSITY,
                    "校级", "院级", "学校", "大学", "学院", "中学"),
            level(CertificateLevel.INDUSTRY,
                    "行业级", "行业协会", "协会", "学会", "认证中心", "hcia", "hcip", "hcie", "阿里云认证", "腾讯云认证"),
            level(CertificateLevel.INSTITUTION,
                    "机构级", "培训机构", "培训中心", "有限公司", "公司")
    );

    public CertificateDtos.ClassificationSuggestion suggest(String name, String issuer) {
        String text = normalize(name) + normalize(issuer);
        if (text.isBlank()) {
            return new CertificateDtos.ClassificationSuggestion(
                    CertificateCategory.OTHER, CertificateLevel.OTHER, 0, true, List.of());
        }

        Match<CertificateCategory> categoryMatch = matchCategory(text);
        CertificateCategory category = categoryMatch == null ? CertificateCategory.OTHER : categoryMatch.value();
        Match<CertificateLevel> levelMatch = matchLevel(text);
        CertificateLevel level = levelMatch == null ? defaultLevel(category) : levelMatch.value();

        Set<String> matched = new LinkedHashSet<>();
        if (categoryMatch != null) matched.addAll(categoryMatch.keywords());
        if (levelMatch != null) matched.addAll(levelMatch.keywords());

        boolean fallback = category == CertificateCategory.OTHER || level == CertificateLevel.OTHER;
        int confidence;
        if (categoryMatch == null) confidence = levelMatch == null ? 0 : 38;
        else if (levelMatch != null) confidence = Math.min(98, 86 + matched.size() * 3);
        else if (level != CertificateLevel.OTHER) confidence = 78;
        else confidence = 66;

        return new CertificateDtos.ClassificationSuggestion(
                category, level, confidence, fallback, matched.stream().limit(4).toList());
    }

    private Match<CertificateCategory> matchCategory(String text) {
        for (CategoryRule rule : CATEGORY_RULES) {
            List<String> matched = matchingKeywords(text, rule.keywords());
            if (!matched.isEmpty()) return new Match<>(rule.category(), matched);
        }
        return null;
    }

    private Match<CertificateLevel> matchLevel(String text) {
        for (LevelRule rule : LEVEL_RULES) {
            List<String> matched = matchingKeywords(text, rule.keywords());
            if (!matched.isEmpty()) return new Match<>(rule.level(), matched);
        }
        return null;
    }

    private List<String> matchingKeywords(String text, List<String> keywords) {
        List<String> matched = new ArrayList<>();
        for (String keyword : keywords) {
            if (text.contains(normalize(keyword))) matched.add(keyword);
        }
        return matched;
    }

    private CertificateLevel defaultLevel(CertificateCategory category) {
        return switch (category) {
            case ACADEMIC_EDUCATION, HONORARY_TITLE, CULTURE_SPORTS, SOCIAL_PRACTICE, VOLUNTEER_SERVICE -> CertificateLevel.UNIVERSITY;
            case PROFESSIONAL_QUALIFICATION, INTELLECTUAL_PROPERTY -> CertificateLevel.NATIONAL;
            case SKILL_CERTIFICATION -> CertificateLevel.INDUSTRY;
            case TRAINING_CERTIFICATE -> CertificateLevel.INSTITUTION;
            default -> CertificateLevel.OTHER;
        };
    }

    private static CategoryRule category(CertificateCategory category, String... keywords) {
        return new CategoryRule(category, List.of(keywords));
    }

    private static LevelRule level(CertificateLevel level, String... keywords) {
        return new LevelRule(level, List.of(keywords));
    }

    private String normalize(String value) {
        return value == null ? "" : value.toLowerCase(Locale.ROOT).replaceAll("[\\s·•_—–-]+", "");
    }
}
