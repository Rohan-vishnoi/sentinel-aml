package com.sentinelaml.service;

import com.sentinelaml.domain.Alert;
import com.sentinelaml.domain.AlertStatus;
import com.sentinelaml.domain.Customer;
import com.sentinelaml.dto.AlertView;
import com.sentinelaml.domain.CaseFile;
import com.sentinelaml.domain.CaseStatus;
import com.sentinelaml.exception.NotFoundException;
import com.sentinelaml.repository.AlertRepository;
import com.sentinelaml.repository.CaseRepository;
import com.sentinelaml.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

@Service
public class AlertService {

    private final AlertRepository alertRepository;
    private final CustomerRepository customerRepository;
    private final CaseRepository caseRepository;
    private final MaskingService maskingService;
    private final AuditService auditService;

    public AlertService(AlertRepository alertRepository, CustomerRepository customerRepository, CaseRepository caseRepository, MaskingService maskingService, AuditService auditService) {
        this.alertRepository = alertRepository;
        this.customerRepository = customerRepository;
        this.caseRepository = caseRepository;
        this.maskingService = maskingService;
        this.auditService = auditService;
    }

    @Transactional
    public Alert upsertAlert(DetectedAlert candidate) {
        Alert alert = alertRepository.findByAlertKey(candidate.alertKey()).orElseGet(Alert::new);
        boolean isNew = alert.getId() == null;
        Customer customer = customerRepository.findByCustomerId(candidate.customerId())
                .orElseThrow(() -> new NotFoundException("Customer not found: " + candidate.customerId()));

        alert.setAlertKey(candidate.alertKey());
        alert.setCustomerId(customer.getCustomerId());
        alert.setCustomerNameMasked(maskingService.maskName(customer.fullName()));
        alert.setAccountId(candidate.accountId());
        alert.setPrimaryRule(candidate.primaryRule());
        alert.setTriggeredRulesCsv(candidate.triggeredRulesCsv());
        alert.setEvidenceCsv(candidate.evidenceCsv());
        alert.setExplanation(candidate.explanation());
        alert.setRiskScore(candidate.riskScore());
        alert.setStatus(candidate.status() == null ? AlertStatus.OPEN.name() : candidate.status());
        alert.setDispositionReason(candidate.dispositionReason());
        alert.setAnalystId(candidate.analystId());
        Alert saved = alertRepository.save(alert);
        if (saved.getCaseId() == null) {
            CaseFile caseFile = caseRepository.findByAlertId(saved.getId()).orElseGet(CaseFile::new);
            caseFile.setCaseNumber(caseFile.getCaseNumber() == null ? "CASE-" + saved.getId().substring(0, 8) : caseFile.getCaseNumber());
            caseFile.setAlertId(saved.getId());
            caseFile.setCustomerId(saved.getCustomerId());
            caseFile.setStatus(CaseStatus.OPEN.name());
            CaseFile savedCase = caseRepository.save(caseFile);
            saved.setCaseId(savedCase.getId());
            alertRepository.save(saved);
        }
        auditService.record("ALERT", saved.getId(), isNew ? "CREATED" : "UPDATED", "system", candidate.explanation());
        return saved;
    }

    @Transactional(readOnly = true)
    public List<AlertView> listAlerts() {
        return alertRepository.findAllByOrderByRiskScoreDescCreatedAtDesc().stream()
                .map(this::toView)
                .toList();
    }

    @Transactional(readOnly = true)
    public AlertView getAlert(String id) {
        return toView(alertRepository.findById(id).orElseThrow(() -> new NotFoundException("Alert not found: " + id)));
    }

    @Transactional
    public AlertView updateDisposition(String id, String status, String analystId, String reason) {
        Alert alert = alertRepository.findById(id).orElseThrow(() -> new NotFoundException("Alert not found: " + id));
        alert.setStatus(status);
        alert.setAnalystId(analystId);
        alert.setDispositionReason(reason);
        Alert saved = alertRepository.save(alert);
        auditService.record("ALERT", saved.getId(), "DISPOSITION", analystId, reason);
        return toView(saved);
    }

    private AlertView toView(Alert alert) {
        return new AlertView(
                alert.getId(),
                alert.getAlertKey(),
                alert.getCustomerId(),
                alert.getCustomerNameMasked(),
                alert.getAccountId(),
                alert.getPrimaryRule(),
                alert.getTriggeredRulesCsv(),
                alert.getEvidenceCsv(),
                alert.getExplanation(),
                alert.getRiskScore(),
                alert.getStatus(),
                alert.getDispositionReason(),
                alert.getAnalystId(),
                alert.getCaseId(),
                alert.getCreatedAt()
        );
    }
}
