package com.zupu.zupu.mapper;

import com.zupu.zupu.entity.Family;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FamilyMapper {
    //查询可访问的家族列表
    List<Family> selectAccessible(@Param("ownerUserId") Integer ownerUserId, @Param("admin") boolean admin);

    //根据id查询单个家族
    Family selectById(@Param("id") Integer id);

    //插入一个家族
    int insert(Family family);

    //更新一个家族
    int update(Family family);

    //删除一个家族
    int deleteById(@Param("id") Integer id);

    //统计可访问的家族数量
    int countAccessible(@Param("ownerUserId") Integer ownerUserId, @Param("admin") boolean admin);
}
