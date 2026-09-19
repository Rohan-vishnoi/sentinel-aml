package com.sentinelaml.dto;

import java.util.List;

public record DashboardView(List<AlertView> alerts, List<CaseView> cases) {
}
