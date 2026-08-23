package com.zupu.zupu.service.impl;

import com.zupu.zupu.dto.FamilyStats;
import com.zupu.zupu.entity.Family;
import com.zupu.zupu.entity.Person;
import com.zupu.zupu.mapper.PersonMapper;
import com.zupu.zupu.security.LoginUser;
import com.zupu.zupu.service.FamilyService;
import com.zupu.zupu.service.StatsService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class StatsServiceImpl implements StatsService {
    private final PersonMapper personMapper;
    private final FamilyService familyService;

    public StatsServiceImpl(PersonMapper personMapper, FamilyService familyService) {
        this.personMapper = personMapper;
        this.familyService = familyService;
    }

    @Override
    @Cacheable(value = "familyStats", key = "#familyId")
    public FamilyStats getFamilyStats(Integer familyId, LoginUser user) {
        Family family = familyService.getAccessibleFamily(familyId, user);
        List<Person> people = personMapper.selectByFamilyId(familyId);

        Map<Integer, Integer> genCount = new TreeMap<>();
        int male = 0;
        int female = 0;
        for (Person p : people) {
            int g = p.getGenerationNo() == null ? 1 : p.getGenerationNo();
            genCount.merge(g, 1, Integer::sum);
            if ("男".equals(p.getGender())) {
                male++;
            } else if ("女".equals(p.getGender())) {
                female++;
            }
        }
        List<FamilyStats.GenerationStat> generations = genCount.entrySet().stream()
                .map(e -> new FamilyStats.GenerationStat(e.getKey(), e.getValue()))
                .toList();
        return new FamilyStats(family.getName(), people.size(), male, female, generations);
    }
}
