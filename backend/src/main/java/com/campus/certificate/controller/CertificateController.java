package com.campus.certificate.controller;

import com.campus.certificate.domain.AwardType;
import com.campus.certificate.domain.CertificateCategory;
import com.campus.certificate.domain.CertificateLevel;
import com.campus.certificate.dto.CertificateDtos;
import com.campus.certificate.service.CertificateClassificationService;
import com.campus.certificate.service.CertificateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/certificates")
@RequiredArgsConstructor
public class CertificateController {
    private final CertificateService certificateService;
    private final CertificateClassificationService classificationService;

    @GetMapping
    public CertificateDtos.PageResponse<CertificateDtos.CertificateResponse> list(
            @AuthenticationPrincipal Long userId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) CertificateCategory category,
            @RequestParam(required = false) CertificateLevel level,
            @RequestParam(required = false) AwardType awardType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "issueDate,desc") String sort
    ) {
        return certificateService.list(userId, keyword, category, level, awardType, page, size, sort);
    }

    @GetMapping("/classification/suggest")
    public CertificateDtos.ClassificationSuggestion suggestClassification(
            @RequestParam String name,
            @RequestParam(required = false) String issuer
    ) {
        return classificationService.suggest(name, issuer);
    }

    @GetMapping("/trash")
    public CertificateDtos.PageResponse<CertificateDtos.CertificateResponse> listTrash(
            @AuthenticationPrincipal Long userId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        return certificateService.listTrash(userId, keyword, page, size);
    }

    @GetMapping("/{id}")
    public CertificateDtos.CertificateResponse get(@AuthenticationPrincipal Long userId, @PathVariable Long id) {
        return certificateService.get(userId, id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CertificateDtos.CertificateResponse create(@AuthenticationPrincipal Long userId,
                                                        @Valid @RequestBody CertificateDtos.UpsertCertificateRequest request) {
        return certificateService.create(userId, request);
    }

    @PutMapping("/{id}")
    public CertificateDtos.CertificateResponse update(@AuthenticationPrincipal Long userId, @PathVariable Long id,
                                                        @Valid @RequestBody CertificateDtos.UpsertCertificateRequest request) {
        return certificateService.update(userId, id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal Long userId, @PathVariable Long id) {
        certificateService.delete(userId, id);
    }

    @PostMapping("/{id}/restore")
    public CertificateDtos.CertificateResponse restore(@AuthenticationPrincipal Long userId, @PathVariable Long id) {
        return certificateService.restore(userId, id);
    }

    @DeleteMapping("/{id}/permanent")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePermanently(@AuthenticationPrincipal Long userId, @PathVariable Long id) {
        certificateService.deletePermanently(userId, id);
    }

    @PostMapping(value = "/{id}/attachment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CertificateDtos.CertificateResponse upload(@AuthenticationPrincipal Long userId, @PathVariable Long id,
                                                        @RequestPart("file") MultipartFile file) {
        return certificateService.upload(userId, id, file);
    }

    @GetMapping("/{id}/attachment")
    public ResponseEntity<FileSystemResource> download(@AuthenticationPrincipal Long userId, @PathVariable Long id) {
        CertificateService.Attachment attachment = certificateService.download(userId, id);
        MediaType mediaType;
        try {
            mediaType = MediaType.parseMediaType(attachment.contentType());
        } catch (InvalidMediaTypeException ex) {
            mediaType = MediaType.APPLICATION_OCTET_STREAM;
        }
        ContentDisposition disposition = ContentDisposition.attachment()
                .filename(attachment.fileName(), StandardCharsets.UTF_8).build();
        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .body(attachment.resource());
    }

    @DeleteMapping("/{id}/attachment")
    public CertificateDtos.CertificateResponse deleteAttachment(@AuthenticationPrincipal Long userId, @PathVariable Long id) {
        return certificateService.deleteAttachment(userId, id);
    }
}

