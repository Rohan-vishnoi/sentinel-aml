package com.sentinelaml.repository;

import com.sentinelaml.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, String> {
    Optional<Account> findByAccountId(String accountId);
    List<Account> findByCustomer_CustomerId(String customerId);
}
