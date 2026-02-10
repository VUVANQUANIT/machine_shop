package com.example.machinesshop.config;

import com.cloudinary.Cloudinary;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration cho Cloudinary.
 * Đọc credentials từ environment variables hoặc application.properties.
 * Chỉ tạo bean khi có đủ config (cloud-name, api-key, api-secret).
 */
@Slf4j
@Configuration
@ConditionalOnProperty(
        prefix = "cloudinary",
        name = {"cloud-name", "api-key", "api-secret"},
        matchIfMissing = false
)
public class CloudinaryConfig {

    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.api-key}")
    private String apiKey;

    @Value("${cloudinary.api-secret}")
    private String apiSecret;

    /**
     * Tạo Cloudinary bean với tên rõ ràng để tránh conflict với các bean khác.
     * Bean name: "cloudinary" (không phải "cloudinaryConfig").
     * @Primary: Ưu tiên bean này nếu có nhiều Cloudinary beans.
     */
    @Bean(name = "cloudinary")
    @Primary
    public Cloudinary cloudinaryBean() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", cloudName);
        config.put("api_key", apiKey);
        config.put("api_secret", apiSecret);
        config.put("secure", "true"); // Luôn dùng HTTPS

        log.info("Cloudinary configured với cloud_name: {}", cloudName);
        return new Cloudinary(config);
    }
}
