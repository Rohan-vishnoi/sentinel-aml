package com.sentinelaml.controller;

import com.sentinelaml.dto.DashboardView;
import com.sentinelaml.service.AlertService;
import com.sentinelaml.service.CaseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final AlertService alertService;
    private final CaseService caseService;

    public DashboardController(AlertService alertService, CaseService caseService) {
        this.alertService = alertService;
        this.caseService = caseService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        DashboardView view = new DashboardView(alertService.listAlerts(), caseService.listCases());
        model.addAttribute("alerts", view.alerts());
        model.addAttribute("cases", view.cases());
        return "dashboard";
    }
}
