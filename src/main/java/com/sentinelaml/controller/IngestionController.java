package com.sentinelaml.controller;

import com.sentinelaml.dto.AccountIngestRequest;
import com.sentinelaml.dto.CustomerIngestRequest;
import com.sentinelaml.dto.TransactionIngestRequest;
import com.sentinelaml.service.IngestionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ingestion")
public class IngestionController {

    private final IngestionService ingestionService;

    public IngestionController(IngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    @PostMapping("/customers")
    public Object ingestCustomers(@Valid @RequestBody List<CustomerIngestRequest> requests) {
        return ingestionService.ingestCustomers(requests);
    }

    @PostMapping("/accounts")
    public Object ingestAccounts(@Valid @RequestBody List<AccountIngestRequest> requests) {
        return ingestionService.ingestAccounts(requests);
    }

    @PostMapping("/transactions")
    public Object ingestTransactions(@Valid @RequestBody List<TransactionIngestRequest> requests) {
        return ingestionService.ingestTransactions(requests);
    }
}
