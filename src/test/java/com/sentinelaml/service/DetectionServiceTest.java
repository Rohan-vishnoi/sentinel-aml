package com.sentinelaml.service;

import com.sentinelaml.domain.Account;
import com.sentinelaml.domain.Customer;
import com.sentinelaml.domain.TransactionRecord;
import com.sentinelaml.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DetectionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private AlertService alertService;
    @Mock
    private RuleConfigService ruleConfigService;
    @InjectMocks
    private DetectionService detectionService;
    @Captor
    private ArgumentCaptor<DetectedAlert> alertCaptor;

    private TransactionRecord baseTransaction;

    @BeforeEach
    void setUp() {
        when(ruleConfigService.getNumeric(eq("CTR_THRESHOLD_BASE"), any()))
                .thenReturn(BigDecimal.valueOf(10000));
        when(ruleConfigService.isEnabled(any(), anyBoolean())).thenReturn(true);
        when(ruleConfigService.getTextSet(eq("HIGH_RISK_JURISDICTIONS"), anySet()))
                .thenReturn(Set.of("IR"));
        when(alertService.upsertAlert(any())).thenAnswer(invocation -> {
            DetectedAlert candidate = invocation.getArgument(0);
            com.sentinelaml.domain.Alert alert = new com.sentinelaml.domain.Alert();
            alert.setId("ALERT-" + candidate.primaryRule());
            return alert;
        });

        Customer customer = new Customer();
        customer.setCustomerId("CUST_00001");
        customer.setFirstName("Krishna");
        customer.setLastName("Sharma");

        Account account = new Account();
        account.setAccountId("ACC_000001");
        account.setCustomer(customer);

        baseTransaction = new TransactionRecord();
        baseTransaction.setTransactionId("TXN_100000");
        baseTransaction.setAccount(account);
        baseTransaction.setTransactionTimestamp(Instant.parse("2026-09-19T10:00:00Z"));
        baseTransaction.setCurrency("INR");
        baseTransaction.setDirection("CREDIT");
        baseTransaction.setTransactionType("DEPOSIT");
        baseTransaction.setAmount(BigDecimal.valueOf(1000));
        baseTransaction.setNormalizedAmountBase(BigDecimal.valueOf(1000));
    }

    @Test
    void flagsCtrThreshold() {
        baseTransaction.setAmount(BigDecimal.valueOf(11000));
        baseTransaction.setNormalizedAmountBase(BigDecimal.valueOf(11000));
        when(transactionRepository.findByAccount_AccountIdAndTransactionTimestampBetween(any(), any(), any()))
                .thenReturn(List.of(baseTransaction));

        detectionService.evaluate(baseTransaction);

        var captor = ArgumentCaptor.forClass(DetectedAlert.class);
        org.mockito.Mockito.verify(alertService).upsertAlert(captor.capture());
        assertThat(captor.getValue().primaryRule()).isEqualTo("CTR_THRESHOLD");
    }

    @Test
    void flagsStructuring() {
        TransactionRecord t1 = copyTransaction("TXN_A", BigDecimal.valueOf(9900), "CREDIT");
        TransactionRecord t2 = copyTransaction("TXN_B", BigDecimal.valueOf(9950), "CREDIT");
        TransactionRecord t3 = copyTransaction("TXN_C", BigDecimal.valueOf(9980), "CREDIT");
        when(transactionRepository.findByAccount_AccountIdAndTransactionTimestampBetween(any(), any(), any()))
                .thenReturn(List.of(t1, t2, t3));

        detectionService.evaluate(t3);

        org.mockito.Mockito.verify(alertService, org.mockito.Mockito.atLeastOnce()).upsertAlert(alertCaptor.capture());
        assertThat(alertCaptor.getAllValues())
                .extracting(DetectedAlert::primaryRule)
                .contains("STRUCTURING");
    }

    @Test
    void flagsHighRiskJurisdiction() {
        baseTransaction.setJurisdiction("IR");
        when(transactionRepository.findByAccount_AccountIdAndTransactionTimestampBetween(any(), any(), any()))
                .thenReturn(List.of(baseTransaction));

        detectionService.evaluate(baseTransaction);

        org.mockito.Mockito.verify(alertService).upsertAlert(alertCaptor.capture());
        assertThat(alertCaptor.getValue().primaryRule()).isEqualTo("HIGH_RISK_JURISDICTION");
    }

    private TransactionRecord copyTransaction(String id, BigDecimal amount, String direction) {
        TransactionRecord copy = new TransactionRecord();
        copy.setTransactionId(id);
        copy.setAccount(baseTransaction.getAccount());
        copy.setTransactionTimestamp(baseTransaction.getTransactionTimestamp());
        copy.setCurrency("INR");
        copy.setDirection(direction);
        copy.setTransactionType("DEPOSIT");
        copy.setAmount(amount);
        copy.setNormalizedAmountBase(amount);
        return copy;
    }
}
