package com.sentinelaml.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity(name = "alerts")
public class Alert extends AuditedEntity {

    @Column(name = "alert_key", nullable = false, unique = true)
    private String alertKey;
    @Column(name = "customer_id", nullable = false)
    private String customerId;
    @Column(name = "customer_name_masked")
    private String customerNameMasked;
    @Column(name = "account_id")
    private String accountId;
    @Column(name = "primary_rule", nullable = false)
    private String primaryRule;
    @Column(name = "triggered_rules_csv", nullable = false, length = 1000)
    private String triggeredRulesCsv;
    @Column(name = "evidence_csv", nullable = false, length = 4000)
    private String evidenceCsv;
    @Column(nullable = false, length = 4000)
    private String explanation;
    @Column(nullable = false)
    private Integer riskScore;
    @Column(nullable = false)
    private String status;
    @Column(name = "disposition_reason", length = 1000)
    private String dispositionReason;
    @Column(name = "analyst_id")
    private String analystId;
    @Column(name = "case_id")
    private String caseId;

    public Alert() {
    }

    public String getAlertKey() {
        return alertKey;
    }

    public void setAlertKey(String alertKey) {
        this.alertKey = alertKey;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerNameMasked() {
        return customerNameMasked;
    }

    public void setCustomerNameMasked(String customerNameMasked) {
        this.customerNameMasked = customerNameMasked;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getPrimaryRule() {
        return primaryRule;
    }

    public void setPrimaryRule(String primaryRule) {
        this.primaryRule = primaryRule;
    }

    public String getTriggeredRulesCsv() {
        return triggeredRulesCsv;
    }

    public void setTriggeredRulesCsv(String triggeredRulesCsv) {
        this.triggeredRulesCsv = triggeredRulesCsv;
    }

    public String getEvidenceCsv() {
        return evidenceCsv;
    }

    public void setEvidenceCsv(String evidenceCsv) {
        this.evidenceCsv = evidenceCsv;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public Integer getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(Integer riskScore) {
        this.riskScore = riskScore;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDispositionReason() {
        return dispositionReason;
    }

    public void setDispositionReason(String dispositionReason) {
        this.dispositionReason = dispositionReason;
    }

    public String getAnalystId() {
        return analystId;
    }

    public void setAnalystId(String analystId) {
        this.analystId = analystId;
    }

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }
}
