package com.sentinelaml.service;

import com.sentinelaml.domain.Account;
import com.sentinelaml.domain.Customer;
import com.sentinelaml.domain.TransactionRecord;
import com.sentinelaml.dto.AccountIngestRequest;
import com.sentinelaml.dto.CustomerIngestRequest;
import com.sentinelaml.dto.TransactionIngestRequest;
import com.sentinelaml.exception.BadRequestException;
import com.sentinelaml.exception.NotFoundException;
import com.sentinelaml.repository.AccountRepository;
import com.sentinelaml.repository.CustomerRepository;
import com.sentinelaml.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class IngestionService {

    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final DetectionService detectionService;
    private final CurrencyConversionService currencyConversionService;

    public IngestionService(CustomerRepository customerRepository, AccountRepository accountRepository, TransactionRepository transactionRepository, DetectionService detectionService, CurrencyConversionService currencyConversionService) {
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.detectionService = detectionService;
        this.currencyConversionService = currencyConversionService;
    }

    @Transactional
    public Customer ingestCustomer(CustomerIngestRequest request) {
        Customer customer = customerRepository.findByCustomerId(request.customerId()).orElseGet(Customer::new);
        customer.setCustomerId(request.customerId());
        customer.setFirstName(request.firstName());
        customer.setLastName(request.lastName());
        customer.setGender(request.gender());
        customer.setDateOfBirth(request.dateOfBirth());
        customer.setAge(request.age());
        customer.setEmail(request.email());
        customer.setPhoneNumber(request.phoneNumber());
        customer.setCity(request.city());
        customer.setState(request.state());
        customer.setCountry(request.country());
        customer.setPostalCode(request.postalCode());
        customer.setOccupation(request.occupation());
        customer.setAnnualIncome(request.annualIncome());
        customer.setMaritalStatus(request.maritalStatus());
        customer.setEducationLevel(request.educationLevel());
        customer.setEmploymentStatus(request.employmentStatus());
        customer.setCustomerSince(request.customerSince());
        customer.setCustomerSegment(request.customerSegment());
        customer.setKycStatus(request.kycStatus());
        customer.setRiskRating(request.riskRating());
        customer.setPoliticallyExposed(Boolean.TRUE.equals(request.politicallyExposed()));
        customer.setPreferredChannel(request.preferredChannel());
        customer.setEmailVerified(Boolean.TRUE.equals(request.emailVerified()));
        customer.setPhoneVerified(Boolean.TRUE.equals(request.phoneVerified()));
        customer.setNumComplaintsLastYear(request.numComplaintsLastYear());
        return customerRepository.save(customer);
    }

    @Transactional
    public Account ingestAccount(AccountIngestRequest request) {
        Customer customer = customerRepository.findByCustomerId(request.customerId())
                .orElseThrow(() -> new NotFoundException("Customer not found: " + request.customerId()));
        Account account = accountRepository.findByAccountId(request.accountId()).orElseGet(Account::new);
        account.setAccountId(request.accountId());
        account.setCustomer(customer);
        account.setAccountType(request.accountType());
        account.setAccountStatus(request.accountStatus());
        account.setCurrency(request.currency());
        account.setOpenDate(request.openDate());
        account.setCloseDate(request.closeDate());
        account.setBranchCode(request.branchCode());
        account.setBranchCity(request.branchCity());
        account.setCurrentBalance(request.currentBalance());
        account.setAvgMonthlyBalance6m(request.avgMonthlyBalance6m());
        account.setCreditLimit(request.creditLimit());
        account.setCreditUtilizationPct(request.creditUtilizationPct());
        account.setOverdraftEnabled(Boolean.TRUE.equals(request.overdraftEnabled()));
        account.setCardType(request.cardType());
        account.setJointAccount(Boolean.TRUE.equals(request.jointAccount()));
        account.setNumLinkedDevices(request.numLinkedDevices());
        account.setMobileBankingEnrolled(Boolean.TRUE.equals(request.mobileBankingEnrolled()));
        account.setLastLoginDate(request.lastLoginDate());
        account.setAvgMonthlyTxnCount(request.avgMonthlyTxnCount());
        account.setAccountTier(request.accountTier());
        return accountRepository.save(account);
    }

    @Transactional
    public TransactionRecord ingestTransaction(TransactionIngestRequest request) {
        Account account = accountRepository.findByAccountId(request.accountId())
                .orElseThrow(() -> new NotFoundException("Account not found: " + request.accountId()));
        if (request.amount().signum() <= 0) {
            throw new BadRequestException("Transaction amount must be positive");
        }

        TransactionRecord transaction = transactionRepository.findByTransactionId(request.transactionId()).orElseGet(TransactionRecord::new);
        transaction.setTransactionId(request.transactionId());
        transaction.setAccount(account);
        transaction.setAmount(request.amount());
        transaction.setCurrency(request.currency());
        transaction.setNormalizedAmountBase(currencyConversionService.normalizeToBase(request.amount(), request.currency(), request.transactionTimestamp().atZone(java.time.ZoneOffset.UTC).toLocalDate()));
        transaction.setCounterparty(request.counterparty());
        transaction.setChannel(request.channel());
        transaction.setJurisdiction(request.jurisdiction());
        transaction.setTransactionTimestamp(request.transactionTimestamp());
        transaction.setDirection(request.direction());
        transaction.setTransactionType(request.transactionType());
        TransactionRecord saved = transactionRepository.save(transaction);
        detectionService.evaluate(saved);
        return saved;
    }

    @Transactional
    public List<TransactionRecord> ingestTransactions(List<TransactionIngestRequest> requests) {
        return requests.stream().map(this::ingestTransaction).toList();
    }

    @Transactional
    public List<Account> ingestAccounts(List<AccountIngestRequest> requests) {
        return requests.stream().map(this::ingestAccount).toList();
    }

    @Transactional
    public List<Customer> ingestCustomers(List<CustomerIngestRequest> requests) {
        return requests.stream().map(this::ingestCustomer).toList();
    }
}
