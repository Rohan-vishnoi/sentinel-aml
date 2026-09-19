package com.sentinelaml.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import java.time.Instant;

@Entity(name = "case_files")
public class CaseFile extends AuditedEntity {

    @Column(name = "case_number", nullable = false, unique = true)
    private String caseNumber;

    @Column(name = "alert_id", nullable = false, unique = true)
    private String alertId;

    @Column(name = "customer_id", nullable = false)
    private String customerId;
    @Column(nullable = false)
    private String status;
    @Column(name = "assigned_analyst")
    private String assignedAnalyst;
    @Column(name = "disposition_reason", length = 1000)
    private String dispositionReason;
    @Column(name = "dispositioned_by")
    private String dispositionedBy;
    @Column(name = "dispositioned_at")
    private Instant dispositionedAt;

    public CaseFile() {
    }

    public String getCaseNumber() {
        return caseNumber;
    }

    public void setCaseNumber(String caseNumber) {
        this.caseNumber = caseNumber;
    }

    public String getAlertId() {
        return alertId;
    }

    public void setAlertId(String alertId) {
        this.alertId = alertId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAssignedAnalyst() {
        return assignedAnalyst;
    }

    public void setAssignedAnalyst(String assignedAnalyst) {
        this.assignedAnalyst = assignedAnalyst;
    }

    public String getDispositionReason() {
        return dispositionReason;
    }

    public void setDispositionReason(String dispositionReason) {
        this.dispositionReason = dispositionReason;
    }

    public String getDispositionedBy() {
        return dispositionedBy;
    }

    public void setDispositionedBy(String dispositionedBy) {
        this.dispositionedBy = dispositionedBy;
    }

    public Instant getDispositionedAt() {
        return dispositionedAt;
    }

    public void setDispositionedAt(Instant dispositionedAt) {
        this.dispositionedAt = dispositionedAt;
    }
}
