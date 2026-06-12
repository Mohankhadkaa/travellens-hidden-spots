package com.example.travellens.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.example.travellens.config.CloudinaryConfiguredCondition;

@Service
@Conditional(CloudinaryConfiguredCondition.class)
public class CloudinaryImageService {

    private static final Logger log = LoggerFactory.getLogger(CloudinaryImageService.class);

    private static final Set<String> ALLOWED_MIME_TYPES = Set.of("image/jpeg", "image/png", "image/webp");
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    private final Cloudinary cloudinary;

    public CloudinaryImageService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

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
            Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            String url = (String) result.get("secure_url");
            String publicId = (String) result.get("public_id");
            return new UploadResult(url, publicId);
        } catch (IOException e) {
            throw new ImageStorageException("Failed to upload image: " + e.getMessage());
        }
    }

    public void delete(String publicId) {
        if (publicId == null || publicId.isBlank()) {
            return;
        }
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            log.error("Failed to delete Cloudinary image (public ID: {}): {}", publicId, e.getMessage());
        }
    }

    public record UploadResult(String url, String publicId) {}
}
