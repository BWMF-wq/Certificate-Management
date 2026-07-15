package com.campus.certificate.controller;

import com.campus.certificate.dto.CertificateDtos;
import com.campus.certificate.service.CertificateService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final CertificateService certificateService;

    @GetMapping
    public CertificateDtos.DashboardResponse dashboard(@AuthenticationPrincipal Long userId) {
        return certificateService.dashboard(userId);
    }
}

