package com.zupu.zupu.service.impl;

import com.zupu.zupu.common.BizException;
import com.zupu.zupu.entity.FamilyEvent;
import com.zupu.zupu.mapper.FamilyEventMapper;
import com.zupu.zupu.security.LoginUser;
import com.zupu.zupu.service.FamilyEventService;
import com.zupu.zupu.service.FamilyService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FamilyEventServiceImpl implements FamilyEventService {
    private final FamilyEventMapper familyEventMapper;
    private final FamilyService familyService;

    public FamilyEventServiceImpl(FamilyEventMapper familyEventMapper, FamilyService familyService) {
        this.familyEventMapper = familyEventMapper;
        this.familyService = familyService;
    }

    @Override
    public List<FamilyEvent> listByFamilyId(Integer familyId, LoginUser user) {
        familyService.getAccessibleFamily(familyId, user);
        return familyEventMapper.selectByFamilyId(familyId);
    }

    @Override
    public FamilyEvent getById(Integer id, LoginUser user) {
        FamilyEvent familyEvent = familyEventMapper.selectById(id);
        if (familyEvent == null) {
            throw new BizException(404, "家族事件不存在");
        }
        familyService.getAccessibleFamily(familyEvent.getFamilyId(), user);
        return familyEvent;
    }

    @Override
    public void create(FamilyEvent familyEvent, LoginUser user) {
        familyService.getAccessibleFamily(familyEvent.getFamilyId(), user);
        familyEventMapper.insert(familyEvent);
    }

    @Override
    public void update(FamilyEvent familyEvent, LoginUser user) {
        familyService.getAccessibleFamily(familyEvent.getFamilyId(), user);
        familyEventMapper.update(familyEvent);
    }

    @Override
    public void delete(Integer id, LoginUser user) {
        FamilyEvent event = familyEventMapper.selectById(id);
        if (event == null) {
            throw new BizException(404, "家族事件不存在");
        }
        familyService.getAccessibleFamily(event.getFamilyId(), user);
        familyEventMapper.deleteById(id);
    }
}
