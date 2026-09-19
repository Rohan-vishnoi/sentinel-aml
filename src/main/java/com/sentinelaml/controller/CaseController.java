package com.sentinelaml.controller;

import com.sentinelaml.dto.CaseView;
import com.sentinelaml.dto.DispositionRequest;
import com.sentinelaml.service.CaseService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cases")
public class CaseController {

    private final CaseService caseService;

    public CaseController(CaseService caseService) {
        this.caseService = caseService;
    }

    @GetMapping
    public List<CaseView> list() {
        return caseService.listCases();
    }

    @GetMapping("/{caseNumber}")
    public CaseView get(@PathVariable String caseNumber) {
        return caseService.getCase(caseNumber);
    }

    @PostMapping("/alerts/{alertId}")
    public CaseView createForAlert(@PathVariable String alertId) {
        return caseService.openCaseForAlert(alertId, "system");
    }

    @PutMapping("/{caseNumber}/disposition")
    public CaseView disposition(@PathVariable String caseNumber, @Valid @RequestBody DispositionRequest request) {
        return caseService.updateDisposition(caseNumber, request);
    }
}
