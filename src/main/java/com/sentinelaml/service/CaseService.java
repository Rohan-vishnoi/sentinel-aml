package com.sentinelaml.service;

import com.sentinelaml.domain.CaseFile;
import com.sentinelaml.domain.CaseStatus;
import com.sentinelaml.dto.CaseView;
import com.sentinelaml.dto.DispositionRequest;
import com.sentinelaml.exception.NotFoundException;
import com.sentinelaml.repository.AlertRepository;
import com.sentinelaml.repository.CaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class CaseService {

    private final CaseRepository caseRepository;
    private final AlertRepository alertRepository;
    private final AuditService auditService;

    public CaseService(CaseRepository caseRepository, AlertRepository alertRepository, AuditService auditService) {
        this.caseRepository = caseRepository;
        this.alertRepository = alertRepository;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public List<CaseView> listCases() {
        return caseRepository.findAll().stream().map(this::toView).toList();
    }

    @Transactional(readOnly = true)
    public CaseView getCase(String caseNumber) {
        return toView(caseRepository.findByCaseNumber(caseNumber)
                .orElseThrow(() -> new NotFoundException("Case not found: " + caseNumber)));
    }

    @Transactional
    public CaseView openCaseForAlert(String alertId, String customerId) {
        CaseFile caseFile = caseRepository.findByAlertId(alertId).orElseGet(CaseFile::new);
        boolean isNew = caseFile.getId() == null;
        caseFile.setCaseNumber(caseFile.getCaseNumber() == null ? "CASE-" + System.currentTimeMillis() : caseFile.getCaseNumber());
        caseFile.setAlertId(alertId);
        caseFile.setCustomerId(customerId);
        caseFile.setStatus(CaseStatus.OPEN.name());
        CaseFile saved = caseRepository.save(caseFile);
        alertRepository.findById(alertId).ifPresent(alert -> {
            alert.setCaseId(saved.getId());
            alert.setStatus(CaseStatus.OPEN.name());
            alertRepository.save(alert);
        });
        auditService.record("CASE", saved.getId(), isNew ? "CREATED" : "UPDATED", "system", "Case opened for alert " + alertId);
        return toView(saved);
    }

    @Transactional
    public CaseView updateDisposition(String caseNumber, DispositionRequest request) {
        CaseFile caseFile = caseRepository.findByCaseNumber(caseNumber)
                .orElseThrow(() -> new NotFoundException("Case not found: " + caseNumber));
        caseFile.setStatus(request.status());
        caseFile.setAssignedAnalyst(request.analystId());
        caseFile.setDispositionReason(request.reason());
        caseFile.setDispositionedBy(request.analystId());
        caseFile.setDispositionedAt(Instant.now());
        CaseFile saved = caseRepository.save(caseFile);
        auditService.record("CASE", saved.getId(), "DISPOSITION", request.analystId(), request.reason());
        return toView(saved);
    }

    private CaseView toView(CaseFile caseFile) {
        return new CaseView(
                caseFile.getId(),
                caseFile.getCaseNumber(),
                caseFile.getAlertId(),
                caseFile.getCustomerId(),
                caseFile.getStatus(),
                caseFile.getAssignedAnalyst(),
                caseFile.getDispositionReason(),
                caseFile.getDispositionedBy(),
                caseFile.getDispositionedAt()
        );
    }
}
