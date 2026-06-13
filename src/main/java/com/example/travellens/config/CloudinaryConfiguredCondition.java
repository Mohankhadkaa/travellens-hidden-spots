package com.example.travellens.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class CloudinaryConfiguredCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return hasText(context, "CLOUDINARY_CLOUD_NAME")
            && hasText(context, "CLOUDINARY_API_KEY")
            && hasText(context, "CLOUDINARY_API_SECRET");
    }

    private boolean hasText(ConditionContext context, String name) {
        String value = context.getEnvironment().getProperty(name);
        return value != null && !value.isBlank();
    }
}
