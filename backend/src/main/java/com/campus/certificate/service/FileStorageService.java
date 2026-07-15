package com.campus.certificate.service;

import com.campus.certificate.config.StorageProperties;
import com.campus.certificate.exception.BusinessException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageService {
    private static final Set<String> ALLOWED_TYPES = Set.of("application/pdf", "image/jpeg", "image/png", "image/webp");
    private static final Map<String, String> EXTENSIONS = Map.of(
            "application/pdf", ".pdf",
            "image/jpeg", ".jpg",
            "image/png", ".png",
            "image/webp", ".webp"
    );

    private final StorageProperties properties;
    private Path root;

    public record StoredFile(String originalName, String storedName, String contentType, long size) {}

    @PostConstruct
    void init() {
        try {
            root = properties.path().toAbsolutePath().normalize();
            Files.createDirectories(root);
        } catch (IOException ex) {
            throw new IllegalStateException("无法初始化附件存储目录", ex);
        }
    }

    public StoredFile store(Long userId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "请选择要上传的附件");
        }
        if (file.getSize() > properties.maxFileSize()) {
            throw new BusinessException(HttpStatus.PAYLOAD_TOO_LARGE, "附件不能超过10MB");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType.toLowerCase())) {
            throw new BusinessException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "仅支持 PDF、JPG、PNG 或 WebP 文件");
        }
        String originalName = sanitizeOriginalName(file.getOriginalFilename());
        String storedName = userId + "_" + UUID.randomUUID() + EXTENSIONS.get(contentType.toLowerCase());
        Path target = safeResolve(storedName);
        try {
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return new StoredFile(originalName, storedName, contentType.toLowerCase(), file.getSize());
        } catch (IOException ex) {
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, "附件保存失败，请重试");
        }
    }

    public Path load(String storedName) {
        Path path = safeResolve(storedName);
        if (!Files.isRegularFile(path)) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "附件不存在");
        }
        return path;
    }

    public void delete(String storedName) {
        if (storedName == null) return;
        try {
            Files.deleteIfExists(safeResolve(storedName));
        } catch (IOException ignored) {
            // Metadata remains authoritative; orphan cleanup can be retried asynchronously.
        }
    }

    private Path safeResolve(String storedName) {
        Path path = root.resolve(storedName).normalize();
        if (!path.startsWith(root)) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "非法文件路径");
        }
        return path;
    }

    private String sanitizeOriginalName(String originalName) {
        String name = originalName == null ? "certificate" : Path.of(originalName).getFileName().toString();
        name = name.replaceAll("[\\r\\n\\t]", "_");
        return name.length() > 200 ? name.substring(name.length() - 200) : name;
    }
}

