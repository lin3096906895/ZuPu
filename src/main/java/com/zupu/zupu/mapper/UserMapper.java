package com.zupu.zupu.mapper;

import com.zupu.zupu.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    //根据ID查询用户
    User selectById(@Param("id") Integer id);

    //根据用户名查询用户
    User selectByUsername(@Param("username") String username);

    //查询所有用户
    List<User> selectAll();

    //新增用户
    int insert(User user);

    //更新用户
    int updateRole(@Param("id") Integer id, @Param("role") String role);

    //更新用户是否启用
    int updateEnabled(@Param("id") Integer id, @Param("enabled") Integer enabled);

    //更新用户头像
    int updateAvatar(@Param("id") Integer id, @Param("avatar") String avatar);

    //更新用户昵称
    int updateDisplayName(@Param("id") Integer id, @Param("displayName") String displayName);

    //更新用户密码
    int updatePasswordHash(@Param("id") Integer id, @Param("passwordHash") String passwordHash);

    //统计所有用户数量
    int countAll();
}
