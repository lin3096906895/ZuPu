package com.zupu.zupu.service.impl;

import com.zupu.zupu.common.BizException;
import com.zupu.zupu.entity.Generation;
import com.zupu.zupu.mapper.GenerationMapper;
import com.zupu.zupu.model.LookupOption;
import com.zupu.zupu.security.LoginUser;
import com.zupu.zupu.service.FamilyService;
import com.zupu.zupu.service.GenerationService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class GenerationServiceImpl implements GenerationService {
    private final GenerationMapper generationMapper;
    private final FamilyService familyService;

    public GenerationServiceImpl(GenerationMapper generationMapper, FamilyService familyService) {
        this.generationMapper = generationMapper;
        this.familyService = familyService;
    }

    @Override
    public List<Generation> listByFamilyId(Integer familyId, LoginUser user) {
        familyService.getAccessibleFamily(familyId, user);
        List<Generation> generations = generationMapper.selectByFamilyId(familyId);
        for (Generation generation : generations) {
            generation.setCanEdit(isAdmin(user));
        }
        return generations;
    }

    @Override
    public Generation getAccessibleGeneration(Integer id, LoginUser user) {
        Generation generation = generationMapper.selectById(id);
        if (generation == null) {
            throw new BizException(404, "世代不存在");
        }
        familyService.getAccessibleFamily(generation.getFamilyId(), user);
        return generation;
    }

    @Override
    public void createGeneration(Generation generation, LoginUser user) {
        validateGeneration(generation);
        familyService.getAccessibleFamily(generation.getFamilyId(), user);
        generationMapper.insert(generation);
    }

    @Override
    public void updateGeneration(Generation generation, LoginUser user) {
        getAccessibleGeneration(generation.getId(), user);
        validateGeneration(generation);
        familyService.getAccessibleFamily(generation.getFamilyId(), user);
        generationMapper.update(generation);
    }

    @Override
    public void deleteGeneration(Integer id, LoginUser user) {
        Generation generation = getAccessibleGeneration(id, user);
        generationMapper.deleteById(generation.getId());
    }

    @Override
    public int countAccessibleGenerations(LoginUser user) {
        return familyService.listAccessibleFamilies(user).stream()
                .mapToInt(family -> generationMapper.countByFamilyId(family.getId()))
                .sum();
    }

    @Override
    public List<LookupOption> listOptionsByFamilyId(Integer familyId, LoginUser user) {
        familyService.getAccessibleFamily(familyId, user);
        return generationMapper.selectOptionsByFamilyId(familyId);
    }

    private void validateGeneration(Generation generation) {
        if (generation.getFamilyId() == null) {
            throw new BizException("请选择所属家族");
        }
        if (!StringUtils.hasText(generation.getTitle())) {
            throw new BizException("世代名称不能为空");
        }
        if (generation.getSortOrder() == null) {
            throw new BizException("世代顺序不能为空");
        }
    }

    private boolean isAdmin(LoginUser user) {
        return user != null && "ADMIN".equalsIgnoreCase(user.getRole());
    }
}
