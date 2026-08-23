package com.zupu.zupu.service.impl;

import com.zupu.zupu.common.BizException;
import com.zupu.zupu.dto.LoginRequest;
import com.zupu.zupu.dto.RefreshRequest;
import com.zupu.zupu.dto.RegisterRequest;
import com.zupu.zupu.dto.TokenResponse;
import com.zupu.zupu.dto.UserInfoVO;
import com.zupu.zupu.entity.User;
import com.zupu.zupu.mapper.UserMapper;
import com.zupu.zupu.security.JwtUtil;
import com.zupu.zupu.security.LoginUser;
import com.zupu.zupu.service.AuthService;
import com.zupu.zupu.util.PasswordUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

@Service
public class AuthServiceImpl implements AuthService {
    private static final String REFRESH_KEY_PREFIX = "auth:refresh:";

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final StringRedisTemplate redisTemplate;
    private final long refreshExpiration;

    public AuthServiceImpl(UserMapper userMapper,
                           PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil,
                           StringRedisTemplate redisTemplate,
                           @Value("${jwt.refresh-expiration}") long refreshExpiration) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.redisTemplate = redisTemplate;
        this.refreshExpiration = refreshExpiration;
    }

    @Override
    public TokenResponse login(LoginRequest request) {
        String username = request.getUsername().trim();
        User user = userMapper.selectByUsername(username);
        if (user == null || !verifyPassword(request.getPassword(), user)) {
            throw new BizException("用户名或密码错误");
        }
        if (user.getEnabled() == null || user.getEnabled() != 1) {
            throw new BizException("账号已被停用，请联系管理员");
        }
        return issueTokens(new LoginUser(user));
    }

    @Override
    public TokenResponse register(RegisterRequest request) {
        String username = request.getUsername().trim();
        String displayName = request.getDisplayName().trim();
        if (userMapper.selectByUsername(username) != null) {
            throw new BizException("用户名已存在");
        }
        User user = new User();
        user.setUsername(username);
        user.setDisplayName(displayName);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");
        user.setEnabled(1);
        userMapper.insert(user);
        return issueTokens(new LoginUser(user));
    }

    @Override
    public TokenResponse refresh(RefreshRequest request) {
        String username;
        try {
            username = jwtUtil.parse(request.getRefreshToken()).get("username", String.class);
        } catch (Exception e) {
            throw new BizException(401, "refreshToken 无效或已过期");
        }
        String stored = redisTemplate.opsForValue().get(REFRESH_KEY_PREFIX + username);
        if (stored == null || !stored.equals(request.getRefreshToken())) {
            throw new BizException(401, "refreshToken 已失效，请重新登录");
        }
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new BizException(401, "用户不存在");
        }
        return issueTokens(new LoginUser(user));
    }

    @Override
    public void logout(String username) {
        if (StringUtils.hasText(username)) {
            redisTemplate.delete(REFRESH_KEY_PREFIX + username);
        }
    }

    @Override
    public UserInfoVO currentUser(LoginUser user) {
        return UserInfoVO.from(user);
    }

    private boolean verifyPassword(String rawPassword, User user) {
        String hash = user.getPasswordHash();
        if (hash != null && hash.startsWith("$2")) {
            return passwordEncoder.matches(rawPassword, hash);
        }
        // 旧 SHA-256 无盐哈希：校验通过后惰性迁移到 BCrypt
        if (PasswordUtils.matches(rawPassword, hash)) {
            userMapper.updatePasswordHash(user.getId(), passwordEncoder.encode(rawPassword));
            return true;
        }
        return false;
    }

    private TokenResponse issueTokens(LoginUser user) {
        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);
        redisTemplate.opsForValue().set(REFRESH_KEY_PREFIX + user.getUsername(), refreshToken,
                refreshExpiration, TimeUnit.MILLISECONDS);

        TokenResponse response = new TokenResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setExpiresIn(refreshExpiration);
        response.setUser(UserInfoVO.from(user));
        return response;
    }
}
