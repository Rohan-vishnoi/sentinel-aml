package com.sentinelaml.repository;

import com.sentinelaml.domain.TransactionRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<TransactionRecord, String> {
    Optional<TransactionRecord> findByTransactionId(String transactionId);
    List<TransactionRecord> findByAccount_AccountIdAndTransactionTimestampBetween(String accountId, Instant from, Instant to);
    List<TransactionRecord> findByAccount_Customer_CustomerIdAndTransactionTimestampBetween(String customerId, Instant from, Instant to);
    List<TransactionRecord> findByAccount_AccountIdAndTransactionTimestampAfter(String accountId, Instant from);
}
