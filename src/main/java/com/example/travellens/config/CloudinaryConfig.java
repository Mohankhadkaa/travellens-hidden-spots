package com.example.travellens.config;

import com.cloudinary.Cloudinary;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    private static final Logger log = LoggerFactory.getLogger(CloudinaryConfig.class);

    @Bean
    @Conditional(CloudinaryConfiguredCondition.class)
    public Cloudinary cloudinary(Environment environment) {
        String cloudName = environment.getRequiredProperty("CLOUDINARY_CLOUD_NAME");
        String apiKey = environment.getRequiredProperty("CLOUDINARY_API_KEY");
        String apiSecret = environment.getRequiredProperty("CLOUDINARY_API_SECRET");

        log.info("Cloudinary configured for cloud: {}", cloudName);
        return new Cloudinary(Map.of(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));
    }
}
