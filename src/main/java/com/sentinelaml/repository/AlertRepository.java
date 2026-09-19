package com.sentinelaml.repository;

import com.sentinelaml.domain.Alert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlertRepository extends JpaRepository<Alert, String> {
    Optional<Alert> findByAlertKey(String alertKey);
    List<Alert> findByCustomerIdOrderByRiskScoreDescCreatedAtDesc(String customerId);
    List<Alert> findAllByOrderByRiskScoreDescCreatedAtDesc();
}
