package com.bookstore.service.cloudinary;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements CloudinaryService {
    private final Cloudinary cloudinary;

    @SuppressWarnings("unchecked")
    public String uploadFile(MultipartFile file) {
        try {
            String publicId = UUID.randomUUID().toString();

            Map<String, Object> options = ObjectUtils.asMap(
                    "public_id", publicId,
                    "folder", "bookstore_covers"
            );

            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), options);
            return uploadResult.get("secure_url").toString();

        } catch (IOException e) {
            throw new RuntimeException("Error when upload image to cloudinary: " + e.getMessage());
        }
    }
}
