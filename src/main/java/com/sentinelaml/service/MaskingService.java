package com.sentinelaml.service;

import org.springframework.stereotype.Service;

@Service
public class MaskingService {

    public String maskName(String name) {
        if (name == null || name.isBlank()) {
            return "";
        }
        if (name.length() <= 2) {
            return name.charAt(0) + "*";
        }
        return name.charAt(0) + "***" + name.charAt(name.length() - 1);
    }

    public String maskCustomerId(String customerId) {
        if (customerId == null || customerId.length() < 4) {
            return customerId;
        }
        return customerId.substring(0, 4) + "****";
    }
}
