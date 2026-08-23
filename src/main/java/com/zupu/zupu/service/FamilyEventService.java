package com.zupu.zupu.service;

import com.zupu.zupu.entity.FamilyEvent;
import com.zupu.zupu.security.LoginUser;

import java.util.List;

public interface FamilyEventService {
    List<FamilyEvent> listByFamilyId(Integer familyId, LoginUser user);

    FamilyEvent getById(Integer id, LoginUser user);

    void create(FamilyEvent familyEvent, LoginUser user);

    void update(FamilyEvent familyEvent, LoginUser user);

    void delete(Integer id, LoginUser user);
}
