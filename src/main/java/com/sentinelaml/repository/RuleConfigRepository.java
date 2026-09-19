package com.sentinelaml.repository;

import com.sentinelaml.domain.RuleConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RuleConfigRepository extends JpaRepository<RuleConfig, String> {
    Optional<RuleConfig> findByConfigKey(String configKey);
}
