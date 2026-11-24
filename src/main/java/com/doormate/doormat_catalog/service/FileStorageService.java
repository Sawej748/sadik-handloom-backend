package com.doormate.doormat_catalog.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final Cloudinary cloudinary;

    // Upload image to Cloudinary and return URL + Public ID
    public Map uploadImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Product image is required");
        }
        return cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap("folder", "doormat_catalog"));
    }

    // Delete old image from Cloudinary when product is deleted or updated
    public void deleteImage(String publicId) {
        try {
            if (publicId != null && !publicId.isEmpty()) {
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            }
        } catch (IOException e) {
            System.err.println("Cloudinary delete failed: " + e.getMessage());
        }
    }
}
