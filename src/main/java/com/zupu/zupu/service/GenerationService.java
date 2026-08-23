package com.zupu.zupu.service;

import com.zupu.zupu.entity.Generation;
import com.zupu.zupu.model.LookupOption;
import com.zupu.zupu.security.LoginUser;

import java.util.List;

public interface GenerationService {
    List<Generation> listByFamilyId(Integer familyId, LoginUser user);

    Generation getAccessibleGeneration(Integer id, LoginUser user);

    void createGeneration(Generation generation, LoginUser user);

    void updateGeneration(Generation generation, LoginUser user);

    void deleteGeneration(Integer id, LoginUser user);

    int countAccessibleGenerations(LoginUser user);

    List<LookupOption> listOptionsByFamilyId(Integer familyId, LoginUser user);
}
