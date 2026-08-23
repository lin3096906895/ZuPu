package com.zupu.zupu.mapper;

import com.zupu.zupu.entity.Generation;
import com.zupu.zupu.model.LookupOption;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GenerationMapper {
    //查询某个家族的所有辈分
    List<Generation> selectByFamilyId(@Param("familyId") Integer familyId);

    //根据ID查询单个辈分
    Generation selectById(@Param("id") Integer id);

    //插入一个辈分
    int insert(Generation generation);

    //更新一个辈分
    int update(Generation generation);

    //根据ID删除一个辈分
    int deleteById(@Param("id") Integer id);

    //统计某个家族的辈分数量
    int countByFamilyId(@Param("familyId") Integer familyId);

    //查询某个家族的辈分选项
    List<LookupOption> selectOptionsByFamilyId(@Param("familyId") Integer familyId);
}
