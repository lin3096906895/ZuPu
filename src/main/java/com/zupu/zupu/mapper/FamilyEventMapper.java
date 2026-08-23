package com.zupu.zupu.mapper;

import com.zupu.zupu.entity.FamilyEvent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FamilyEventMapper {
    //根据ID查询事件
    FamilyEvent selectById(@Param("id") Integer id);

    //查询某个家庭的所有事件
    List<FamilyEvent> selectByFamilyId(@Param("familyId") Integer familyId);

    //新增事件
    int insert(FamilyEvent familyEvent);

    //更新事件
    int update(FamilyEvent familyEvent);

    //删除事件
    int deleteById(@Param("id") Integer id);
}
