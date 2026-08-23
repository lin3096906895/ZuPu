package com.zupu.zupu.mapper;

import com.zupu.zupu.entity.Marriage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MarriageMapper {
    List<Marriage> selectByFamilyId(@Param("familyId") Integer familyId);

    List<Marriage> selectByPersonId(@Param("personId") Integer personId);

    Marriage selectById(@Param("id") Integer id);

    int insert(Marriage marriage);

    int update(Marriage marriage);

    int deleteById(@Param("id") Integer id);
}
