package com.sentinelaml.repository;

import com.sentinelaml.domain.CaseFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CaseRepository extends JpaRepository<CaseFile, String> {
    Optional<CaseFile> findByAlertId(String alertId);
    Optional<CaseFile> findByCaseNumber(String caseNumber);
}
