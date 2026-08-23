package com.zupu.zupu.service.impl;

import com.zupu.zupu.common.BizException;
import com.zupu.zupu.entity.Marriage;
import com.zupu.zupu.entity.Person;
import com.zupu.zupu.mapper.MarriageMapper;
import com.zupu.zupu.mapper.PersonMapper;
import com.zupu.zupu.security.LoginUser;
import com.zupu.zupu.service.FamilyService;
import com.zupu.zupu.service.MarriageService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MarriageServiceImpl implements MarriageService {
    private final MarriageMapper marriageMapper;
    private final PersonMapper personMapper;
    private final FamilyService familyService;

    public MarriageServiceImpl(MarriageMapper marriageMapper, PersonMapper personMapper, FamilyService familyService) {
        this.marriageMapper = marriageMapper;
        this.personMapper = personMapper;
        this.familyService = familyService;
    }

    @Override
    public List<Marriage> listByFamilyId(Integer familyId, LoginUser user) {
        familyService.getAccessibleFamily(familyId, user);
        return marriageMapper.selectByFamilyId(familyId);
    }

    @Override
    public List<Marriage> listByPersonId(Integer personId, LoginUser user) {
        Person person = personMapper.selectById(personId);
        if (person == null) {
            throw new BizException(404, "人物不存在");
        }
        familyService.getAccessibleFamily(person.getFamilyId(), user);
        return marriageMapper.selectByPersonId(personId);
    }

    @Override
    public Marriage getById(Integer id, LoginUser user) {
        Marriage marriage = marriageMapper.selectById(id);
        if (marriage == null) {
            throw new BizException(404, "婚姻记录不存在");
        }
        familyService.getAccessibleFamily(marriage.getFamilyId(), user);
        return marriage;
    }

    @Override
    @CacheEvict(value = {"familyTree", "familyStats"}, allEntries = true)
    public void create(Marriage marriage, LoginUser user) {
        validate(marriage);
        familyService.getAccessibleFamily(marriage.getFamilyId(), user);
        if (marriage.getMarriageOrder() == null) {
            marriage.setMarriageOrder(1);
        }
        if (marriage.getStatus() == null) {
            marriage.setStatus("MARRIED");
        }
        if (marriage.getIsPrimary() == null) {
            marriage.setIsPrimary(1);
        }
        marriageMapper.insert(marriage);
    }

    @Override
    @CacheEvict(value = {"familyTree", "familyStats"}, allEntries = true)
    public void update(Marriage marriage, LoginUser user) {
        getById(marriage.getId(), user);
        validate(marriage);
        marriageMapper.update(marriage);
    }

    @Override
    @CacheEvict(value = {"familyTree", "familyStats"}, allEntries = true)
    public void delete(Integer id, LoginUser user) {
        getById(id, user);
        marriageMapper.deleteById(id);
    }

    private void validate(Marriage marriage) {
        if (marriage.getFamilyId() == null) {
            throw new BizException("请选择所属家族");
        }
        if (marriage.getHusbandId() == null || marriage.getWifeId() == null) {
            throw new BizException("夫妻双方不能为空");
        }
        if (marriage.getHusbandId().equals(marriage.getWifeId())) {
            throw new BizException("夫妻不能是同一人");
        }
        Person husband = personMapper.selectById(marriage.getHusbandId());
        Person wife = personMapper.selectById(marriage.getWifeId());
        if (husband == null || wife == null) {
            throw new BizException("人物不存在");
        }
        if (!husband.getFamilyId().equals(marriage.getFamilyId()) || !wife.getFamilyId().equals(marriage.getFamilyId())) {
            throw new BizException("夫妻双方必须属于同一家族");
        }
    }
}
