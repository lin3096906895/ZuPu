package com.zupu.zupu.service;

import com.zupu.zupu.model.DashboardSummary;
import com.zupu.zupu.security.LoginUser;

public interface DashboardService {
    DashboardSummary getSummary(LoginUser user);
}
