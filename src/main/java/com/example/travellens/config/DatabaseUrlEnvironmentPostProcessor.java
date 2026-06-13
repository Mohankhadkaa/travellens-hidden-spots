package com.example.travellens.config;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

public class DatabaseUrlEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String configuredUrl = environment.getProperty("SPRING_DATASOURCE_URL");
        String databaseUrl = configuredUrl != null && !configuredUrl.isBlank()
                ? configuredUrl
                : environment.getProperty("DATABASE_URL");

        if (databaseUrl == null || databaseUrl.isBlank() || databaseUrl.startsWith("jdbc:")) {
            return;
        }

        if (!databaseUrl.startsWith("postgres://") && !databaseUrl.startsWith("postgresql://")) {
            return;
        }

        URI uri = URI.create(databaseUrl.replaceFirst("^postgres://", "postgresql://"));
        String host = uri.getHost();
        int port = uri.getPort() == -1 ? 5432 : uri.getPort();
        String database = uri.getPath() != null ? uri.getPath() : "";
        String query = uri.getQuery() != null ? "?" + uri.getQuery() : "";

        Map<String, Object> properties = new HashMap<>();
        properties.put("spring.datasource.url", "jdbc:postgresql://" + host + ":" + port + database + query);

        String userInfo = uri.getUserInfo();
        if (userInfo != null && !userInfo.isBlank()) {
            String[] credentials = userInfo.split(":", 2);
            properties.put("spring.datasource.username", decode(credentials[0]));
            if (credentials.length > 1) {
                properties.put("spring.datasource.password", decode(credentials[1]));
            }
        }

        environment.getPropertySources().addFirst(new MapPropertySource("renderDatabaseUrl", properties));
    }

    private String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
