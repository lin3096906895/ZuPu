package com.zupu.zupu.mapper;

import com.zupu.zupu.entity.Person;
import com.zupu.zupu.model.LookupOption;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PersonMapper {
    List<Person> selectByFamilyId(@Param("familyId") Integer familyId);

    List<Person> selectByFilters(@Param("familyId") Integer familyId, @Param("keyword") String keyword);

    Person selectById(@Param("id") Integer id);

    int insert(Person person);

    int update(Person person);

    int deleteById(@Param("id") Integer id);

    int countByFamilyId(@Param("familyId") Integer familyId);

    int countAccessible(@Param("ownerUserId") Integer ownerUserId, @Param("admin") boolean admin);

    List<LookupOption> selectParentOptions(@Param("familyId") Integer familyId, @Param("excludeId") Integer excludeId);

    List<String> selectSpouseNames(@Param("id") Integer id);

    List<Person> selectChildren(@Param("parentId") Integer parentId);

    List<Person> selectRoots(@Param("familyId") Integer familyId);
}
