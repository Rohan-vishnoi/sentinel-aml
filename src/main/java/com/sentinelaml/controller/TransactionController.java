package com.sentinelaml.controller;

import com.sentinelaml.dto.TransactionIngestRequest;
import com.sentinelaml.service.IngestionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final IngestionService ingestionService;

    public TransactionController(IngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    @PostMapping
    public Object createTransaction(@Valid @RequestBody TransactionIngestRequest request) {
        return ingestionService.ingestTransaction(request);
    }
}
