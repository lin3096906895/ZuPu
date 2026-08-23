package com.zupu.zupu.controller;

import com.zupu.zupu.common.Result;
import com.zupu.zupu.dto.FamilyStats;
import com.zupu.zupu.security.LoginUser;
import com.zupu.zupu.service.StatsService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stats")
public class StatsController {
    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping
    public Result<FamilyStats> stats(@RequestParam Integer familyId, @AuthenticationPrincipal LoginUser user) {
        return Result.success(statsService.getFamilyStats(familyId, user));
    }
}
