package com.zupu.zupu.service.impl;

import com.zupu.zupu.common.BizException;
import com.zupu.zupu.entity.User;
import com.zupu.zupu.mapper.UserMapper;
import com.zupu.zupu.security.LoginUser;
import com.zupu.zupu.service.UserService;
import com.zupu.zupu.util.PasswordUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> listUsers() {
        return userMapper.selectAll();
    }

    @Override
    public void updateRole(Integer id, String role, LoginUser user) {
        requireAdmin(user);
        if (!"ADMIN".equalsIgnoreCase(role) && !"USER".equalsIgnoreCase(role)) {
            throw new BizException("角色值不合法");
        }
        userMapper.updateRole(id, role.toUpperCase());
    }

    @Override
    public void toggleEnabled(Integer id, LoginUser user) {
        requireAdmin(user);
        if (user.getId().equals(id)) {
            throw new BizException("不能停用当前登录管理员");
        }
        User target = userMapper.selectById(id);
        if (target == null) {
            throw new BizException(404, "用户不存在");
        }
        userMapper.updateEnabled(id, target.getEnabled() != null && target.getEnabled() == 1 ? 0 : 1);
    }

    @Override
    public void updateAvatar(Integer id, String avatar, LoginUser user) {
        if (!user.getId().equals(id) && !isAdmin(user)) {
            throw new BizException(403, "只有管理员或用户本人可以更新头像");
        }
        userMapper.updateAvatar(id, avatar);
    }

    @Override
    public void updateDisplayName(Integer id, String displayName, LoginUser user) {
        if (!user.getId().equals(id) && !isAdmin(user)) {
            throw new BizException(403, "只有管理员或用户本人可以更新昵称");
        }
        if (!StringUtils.hasText(displayName)) {
            throw new BizException("昵称不能为空");
        }
        userMapper.updateDisplayName(id, displayName.trim());
    }

    @Override
    public void updatePassword(Integer id, String oldPassword, String newPassword, LoginUser user) {
        if (!user.getId().equals(id) && !isAdmin(user)) {
            throw new BizException(403, "只有管理员或用户本人可以更新密码");
        }
        if (newPassword == null || newPassword.length() < 6) {
            throw new BizException("新密码长度至少 6 位");
        }
        User target = userMapper.selectById(id);
        if (target == null) {
            throw new BizException(404, "用户不存在");
        }
        if (!isAdmin(user) && !verifyPassword(oldPassword, target)) {
            throw new BizException("旧密码错误");
        }
        userMapper.updatePasswordHash(id, passwordEncoder.encode(newPassword));
    }

    @Override
    public int countUsers() {
        return userMapper.countAll();
    }

    private boolean verifyPassword(String rawPassword, User user) {
        String hash = user.getPasswordHash();
        if (hash != null && hash.startsWith("$2")) {
            return passwordEncoder.matches(rawPassword, hash);
        }
        return PasswordUtils.matches(rawPassword, hash);
    }

    private void requireAdmin(LoginUser user) {
        if (!isAdmin(user)) {
            throw new BizException(403, "只有管理员可以执行该操作");
        }
    }

    private boolean isAdmin(LoginUser user) {
        return user != null && "ADMIN".equalsIgnoreCase(user.getRole());
    }
}
