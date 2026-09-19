package com.sentinelaml.controller;

import com.sentinelaml.domain.RuleConfig;
import com.sentinelaml.dto.RuleConfigRequest;
import com.sentinelaml.repository.RuleConfigRepository;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/rules")
public class AdminRuleController {

    private final RuleConfigRepository ruleConfigRepository;

    public AdminRuleController(RuleConfigRepository ruleConfigRepository) {
        this.ruleConfigRepository = ruleConfigRepository;
    }

    @GetMapping
    public List<RuleConfig> list() {
        return ruleConfigRepository.findAll();
    }

    @PostMapping
    public RuleConfig create(@Valid @RequestBody RuleConfigRequest request) {
        RuleConfig config = new RuleConfig();
        config.setConfigKey(request.configKey());
        config.setEnabled(Boolean.TRUE.equals(request.enabled()));
        config.setNumericValue(request.numericValue());
        config.setTextValue(request.textValue());
        config.setDescription(request.description());
        return ruleConfigRepository.save(config);
    }

    @PutMapping("/{configKey}")
    public RuleConfig update(@PathVariable String configKey, @Valid @RequestBody RuleConfigRequest request) {
        RuleConfig config = ruleConfigRepository.findByConfigKey(configKey).orElseGet(RuleConfig::new);
        config.setConfigKey(configKey);
        config.setEnabled(Boolean.TRUE.equals(request.enabled()));
        config.setNumericValue(request.numericValue());
        config.setTextValue(request.textValue());
        config.setDescription(request.description());
        return ruleConfigRepository.save(config);
    }
}
