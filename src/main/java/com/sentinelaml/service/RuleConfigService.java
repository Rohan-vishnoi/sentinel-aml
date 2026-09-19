package com.sentinelaml.service;

import com.sentinelaml.domain.RuleConfig;
import com.sentinelaml.repository.RuleConfigRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RuleConfigService {

    private final RuleConfigRepository ruleConfigRepository;

    public RuleConfigService(RuleConfigRepository ruleConfigRepository) {
        this.ruleConfigRepository = ruleConfigRepository;
    }

    public boolean isEnabled(String key, boolean defaultValue) {
        return ruleConfigRepository.findByConfigKey(key).map(RuleConfig::isEnabled).orElse(defaultValue);
    }

    public BigDecimal getNumeric(String key, BigDecimal defaultValue) {
        return ruleConfigRepository.findByConfigKey(key).map(RuleConfig::getNumericValue).orElse(defaultValue);
    }

    public String getText(String key, String defaultValue) {
        return ruleConfigRepository.findByConfigKey(key).map(RuleConfig::getTextValue).orElse(defaultValue);
    }

    public Set<String> getTextSet(String key, Set<String> defaultValue) {
        String value = getText(key, null);
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        List<String> items = Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList();
        return items.isEmpty() ? defaultValue : new HashSet<>(items);
    }
}
