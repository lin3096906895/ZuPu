package com.zupu.zupu.service;

import com.zupu.zupu.entity.User;
import com.zupu.zupu.security.LoginUser;

import java.util.List;

public interface UserService {
    List<User> listUsers();

    void updateRole(Integer id, String role, LoginUser user);

    void toggleEnabled(Integer id, LoginUser user);

    void updateAvatar(Integer id, String avatar, LoginUser user);

    void updateDisplayName(Integer id, String displayName, LoginUser user);

    void updatePassword(Integer id, String oldPassword, String newPassword, LoginUser user);

    int countUsers();
}
