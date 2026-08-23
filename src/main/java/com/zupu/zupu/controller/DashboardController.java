package com.zupu.zupu.controller;

import com.zupu.zupu.common.Result;
import com.zupu.zupu.model.DashboardSummary;
import com.zupu.zupu.security.LoginUser;
import com.zupu.zupu.service.DashboardService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public Result<DashboardSummary> summary(@AuthenticationPrincipal LoginUser user) {
        return Result.success(dashboardService.getSummary(user));
    }
}
