package com.sentinelaml.controller;

import com.sentinelaml.dto.AlertView;
import com.sentinelaml.dto.DispositionRequest;
import com.sentinelaml.service.AlertService;
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
@RequestMapping("/api/v1/alerts")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping
    public List<AlertView> list() {
        return alertService.listAlerts();
    }

    @GetMapping("/{id}")
    public AlertView get(@PathVariable String id) {
        return alertService.getAlert(id);
    }

    @PutMapping("/{id}/disposition")
    public AlertView disposition(@PathVariable String id, @Valid @RequestBody DispositionRequest request) {
        return alertService.updateDisposition(id, request.status(), request.analystId(), request.reason());
    }
}
