package com.sentinelaml.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "sentinel")
public record SentinelProperties(Security security, String baseCurrency, boolean seedEnabled) {
    public record Security(String adminUsername, String adminPassword, String analystUsername, String analystPassword) {
    }
}
