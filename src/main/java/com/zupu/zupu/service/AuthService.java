package com.zupu.zupu.service;

import com.zupu.zupu.dto.LoginRequest;
import com.zupu.zupu.dto.RefreshRequest;
import com.zupu.zupu.dto.RegisterRequest;
import com.zupu.zupu.dto.TokenResponse;
import com.zupu.zupu.dto.UserInfoVO;
import com.zupu.zupu.security.LoginUser;

public interface AuthService {
    TokenResponse login(LoginRequest request);

    TokenResponse register(RegisterRequest request);

    TokenResponse refresh(RefreshRequest request);

    void logout(String username);

    UserInfoVO currentUser(LoginUser user);
}
