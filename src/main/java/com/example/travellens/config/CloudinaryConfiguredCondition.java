package com.example.travellens.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class CloudinaryConfiguredCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return System.getenv("CLOUDINARY_CLOUD_NAME") != null
            && System.getenv("CLOUDINARY_API_KEY") != null
            && System.getenv("CLOUDINARY_API_SECRET") != null;
    }
}
