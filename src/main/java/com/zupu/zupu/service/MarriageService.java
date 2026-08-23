package com.zupu.zupu.service;

import com.zupu.zupu.entity.Marriage;
import com.zupu.zupu.security.LoginUser;

import java.util.List;

public interface MarriageService {
    List<Marriage> listByFamilyId(Integer familyId, LoginUser user);

    List<Marriage> listByPersonId(Integer personId, LoginUser user);

    Marriage getById(Integer id, LoginUser user);

    void create(Marriage marriage, LoginUser user);

    void update(Marriage marriage, LoginUser user);

    void delete(Integer id, LoginUser user);
}
