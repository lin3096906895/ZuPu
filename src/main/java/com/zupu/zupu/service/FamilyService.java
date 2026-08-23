package com.zupu.zupu.service;

import com.zupu.zupu.entity.Family;
import com.zupu.zupu.security.LoginUser;

import java.util.List;

public interface FamilyService {
    List<Family> listAccessibleFamilies(LoginUser user);

    Family getAccessibleFamily(Integer id, LoginUser user);

    Family getById(Integer id);

    void createFamily(Family family, LoginUser user);

    void updateFamily(Family family, LoginUser user);

    void deleteFamily(Integer id, LoginUser user);

    int countAccessibleFamilies(LoginUser user);
}
