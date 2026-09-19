package com.sentinelaml.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity(name = "accounts")
public class Account extends AuditedEntity {

    @Column(name = "account_id", nullable = false, unique = true)
    private String accountId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    private String accountType;
    private String accountStatus;
    private String currency;
    private LocalDate openDate;
    private LocalDate closeDate;
    private String branchCode;
    private String branchCity;
    @Column(name = "current_balance")
    private BigDecimal currentBalance;
    @Column(name = "avg_monthly_balance_6m")
    private BigDecimal avgMonthlyBalance6m;
    @Column(name = "credit_limit")
    private BigDecimal creditLimit;
    @Column(name = "credit_utilization_pct")
    private BigDecimal creditUtilizationPct;
    @Column(name = "overdraft_enabled")
    private boolean overdraftEnabled;
    private String cardType;
    @Column(name = "is_joint_account")
    private boolean jointAccount;
    @Column(name = "num_linked_devices")
    private Integer numLinkedDevices;
    @Column(name = "mobile_banking_enrolled")
    private boolean mobileBankingEnrolled;
    @Column(name = "last_login_date")
    private LocalDate lastLoginDate;
    @Column(name = "avg_monthly_txn_count")
    private Integer avgMonthlyTxnCount;
    private String accountTier;

    public Account() {
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public LocalDate getOpenDate() {
        return openDate;
    }

    public void setOpenDate(LocalDate openDate) {
        this.openDate = openDate;
    }

    public LocalDate getCloseDate() {
        return closeDate;
    }

    public void setCloseDate(LocalDate closeDate) {
        this.closeDate = closeDate;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getBranchCity() {
        return branchCity;
    }

    public void setBranchCity(String branchCity) {
        this.branchCity = branchCity;
    }

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(BigDecimal currentBalance) {
        this.currentBalance = currentBalance;
    }

    public BigDecimal getAvgMonthlyBalance6m() {
        return avgMonthlyBalance6m;
    }

    public void setAvgMonthlyBalance6m(BigDecimal avgMonthlyBalance6m) {
        this.avgMonthlyBalance6m = avgMonthlyBalance6m;
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }

    public BigDecimal getCreditUtilizationPct() {
        return creditUtilizationPct;
    }

    public void setCreditUtilizationPct(BigDecimal creditUtilizationPct) {
        this.creditUtilizationPct = creditUtilizationPct;
    }

    public boolean isOverdraftEnabled() {
        return overdraftEnabled;
    }

    public void setOverdraftEnabled(boolean overdraftEnabled) {
        this.overdraftEnabled = overdraftEnabled;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public boolean isJointAccount() {
        return jointAccount;
    }

    public void setJointAccount(boolean jointAccount) {
        this.jointAccount = jointAccount;
    }

    public Integer getNumLinkedDevices() {
        return numLinkedDevices;
    }

    public void setNumLinkedDevices(Integer numLinkedDevices) {
        this.numLinkedDevices = numLinkedDevices;
    }

    public boolean isMobileBankingEnrolled() {
        return mobileBankingEnrolled;
    }

    public void setMobileBankingEnrolled(boolean mobileBankingEnrolled) {
        this.mobileBankingEnrolled = mobileBankingEnrolled;
    }

    public LocalDate getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(LocalDate lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public Integer getAvgMonthlyTxnCount() {
        return avgMonthlyTxnCount;
    }

    public void setAvgMonthlyTxnCount(Integer avgMonthlyTxnCount) {
        this.avgMonthlyTxnCount = avgMonthlyTxnCount;
    }

    public String getAccountTier() {
        return accountTier;
    }

    public void setAccountTier(String accountTier) {
        this.accountTier = accountTier;
    }
}
