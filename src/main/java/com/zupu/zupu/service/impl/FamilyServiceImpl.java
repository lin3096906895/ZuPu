package com.zupu.zupu.service.impl;

import com.zupu.zupu.common.BizException;
import com.zupu.zupu.entity.Family;
import com.zupu.zupu.mapper.FamilyMapper;
import com.zupu.zupu.security.LoginUser;
import com.zupu.zupu.service.FamilyService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class FamilyServiceImpl implements FamilyService {
    private final FamilyMapper familyMapper;

    public FamilyServiceImpl(FamilyMapper familyMapper) {
        this.familyMapper = familyMapper;
    }

    @Override
    public List<Family> listAccessibleFamilies(LoginUser user) {
        if (user == null) {
            return List.of();
        }
        List<Family> families = familyMapper.selectAccessible(user.getId(), isAdmin(user));
        for (Family family : families) {
            family.setCanEdit(isAdmin(user) || family.getOwnerUserId().equals(user.getId()));
        }
        return families;
    }

    @Override
    public Family getAccessibleFamily(Integer id, LoginUser user) {
        Family family = familyMapper.selectById(id);
        if (family == null) {
            throw new BizException(404, "家族不存在");
        }
        if (!isAdmin(user) && (user == null || !user.getId().equals(family.getOwnerUserId()))) {
            throw new BizException(403, "权限不足，只能操作自己创建的家族");
        }
        return family;
    }

    @Override
    public Family getById(Integer id) {
        return familyMapper.selectById(id);
    }

    @Override
    public void createFamily(Family family, LoginUser user) {
        validateFamily(family);
        family.setOwnerUserId(user.getId());
        familyMapper.insert(family);
    }

    @Override
    public void updateFamily(Family family, LoginUser user) {
        Family existing = getAccessibleFamily(family.getId(), user);
        validateFamily(family);
        family.setOwnerUserId(existing.getOwnerUserId());
        if (family.getStatus() == null) {
            family.setStatus(existing.getStatus());
        }
        familyMapper.update(family);
    }

    @Override
    public void deleteFamily(Integer id, LoginUser user) {
        getAccessibleFamily(id, user);
        familyMapper.deleteById(id);
    }

    @Override
    public int countAccessibleFamilies(LoginUser user) {
        if (user == null) {
            return 0;
        }
        return familyMapper.countAccessible(user.getId(), isAdmin(user));
    }

    private void validateFamily(Family family) {
        if (!StringUtils.hasText(family.getName())) {
            throw new BizException("家族名称不能为空");
        }
        if (!StringUtils.hasText(family.getAncestorName())) {
            throw new BizException("始祖名称不能为空");
        }
        if (family.getStatus() == null) {
            family.setStatus(1);
        }
    }

    private boolean isAdmin(LoginUser user) {
        return user != null && "ADMIN".equalsIgnoreCase(user.getRole());
    }
}
