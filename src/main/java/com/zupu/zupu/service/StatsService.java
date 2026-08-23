package com.zupu.zupu.service;

import com.zupu.zupu.dto.FamilyStats;
import com.zupu.zupu.security.LoginUser;

public interface StatsService {
    FamilyStats getFamilyStats(Integer familyId, LoginUser user);
}
