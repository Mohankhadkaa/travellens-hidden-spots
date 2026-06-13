package com.example.travellens.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class LocalImageService {

    private static final Set<String> ALLOWED_MIME_TYPES = Set.of("image/jpeg", "image/png", "image/webp");
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    private final Path uploadDir = Paths.get("uploads").toAbsolutePath().normalize();

    public UploadResult upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType)) {
            throw new ImageStorageException("Only JPG, PNG, and WEBP images are allowed");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ImageStorageException("Image must be less than 5 MB");
        }

        try {
            Files.createDirectories(uploadDir);
            String extension = extensionFor(contentType, file.getOriginalFilename());
            String fileName = UUID.randomUUID() + extension;
            Path destination = uploadDir.resolve(fileName).normalize();
            if (!destination.startsWith(uploadDir)) {
                throw new ImageStorageException("Invalid image path");
            }
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
            return new UploadResult(fileName, "/uploads/" + fileName);
        } catch (IOException e) {
            throw new ImageStorageException("Failed to save image: " + e.getMessage());
        }
    }

    public void delete(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return;
        }
        try {
            Path destination = uploadDir.resolve(fileName).normalize();
            if (destination.startsWith(uploadDir)) {
                Files.deleteIfExists(destination);
            }
        } catch (IOException ignored) {
            // Image cleanup should not block post updates or deletes.
        }
    }

    private String extensionFor(String contentType, String originalFilename) {
        String cleanName = StringUtils.cleanPath(originalFilename != null ? originalFilename : "");
        int dot = cleanName.lastIndexOf('.');
        if (dot >= 0) {
            String extension = cleanName.substring(dot).toLowerCase();
            if (Set.of(".jpg", ".jpeg", ".png", ".webp").contains(extension)) {
                return extension;
            }
        }
        return switch (contentType) {
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> ".jpg";
        };
    }

    public record UploadResult(String fileName, String url) {}
}
