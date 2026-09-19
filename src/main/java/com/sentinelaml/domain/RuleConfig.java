package com.sentinelaml.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import java.math.BigDecimal;

@Entity(name = "rule_configs")
public class RuleConfig extends AuditedEntity {

    @Column(name = "config_key", nullable = false, unique = true)
    private String configKey;

    @Column(nullable = false)
    private boolean enabled;

    private BigDecimal numericValue;

    @Column(length = 4000)
    private String textValue;

    @Column(length = 1000)
    private String description;

    public RuleConfig() {
    }

    public String getConfigKey() {
        return configKey;
    }

    public void setConfigKey(String configKey) {
        this.configKey = configKey;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public BigDecimal getNumericValue() {
        return numericValue;
    }

    public void setNumericValue(BigDecimal numericValue) {
        this.numericValue = numericValue;
    }

    public String getTextValue() {
        return textValue;
    }

    public void setTextValue(String textValue) {
        this.textValue = textValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
