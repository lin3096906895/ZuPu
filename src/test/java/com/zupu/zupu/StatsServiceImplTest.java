package com.zupu.zupu;

import com.zupu.zupu.dto.FamilyStats;
import com.zupu.zupu.entity.Family;
import com.zupu.zupu.entity.Person;
import com.zupu.zupu.mapper.PersonMapper;
import com.zupu.zupu.security.LoginUser;
import com.zupu.zupu.service.FamilyService;
import com.zupu.zupu.service.impl.StatsServiceImpl;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StatsServiceImplTest {

    @Test
    void computesStatsByGenerationAndGender() {
        PersonMapper personMapper = mock(PersonMapper.class);
        FamilyService familyService = mock(FamilyService.class);
        LoginUser user = mock(LoginUser.class);

        Family family = new Family();
        family.setName("测试家族");
        when(familyService.getAccessibleFamily(1, user)).thenReturn(family);

        Person p1 = new Person();
        p1.setGenerationNo(1);
        p1.setGender("男");
        Person p2 = new Person();
        p2.setGenerationNo(2);
        p2.setGender("女");
        Person p3 = new Person();
        p3.setGenerationNo(2);
        p3.setGender("男");
        when(personMapper.selectByFamilyId(1)).thenReturn(List.of(p1, p2, p3));

        StatsServiceImpl service = new StatsServiceImpl(personMapper, familyService);
        FamilyStats stats = service.getFamilyStats(1, user);

        assertThat(stats.total()).isEqualTo(3);
        assertThat(stats.male()).isEqualTo(2);
        assertThat(stats.female()).isEqualTo(1);
        assertThat(stats.generations()).hasSize(2);
    }
}
