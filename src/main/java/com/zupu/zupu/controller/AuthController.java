package com.zupu.zupu.controller;

import com.zupu.zupu.common.Result;
import com.zupu.zupu.dto.LoginRequest;
import com.zupu.zupu.dto.RefreshRequest;
import com.zupu.zupu.dto.RegisterRequest;
import com.zupu.zupu.dto.TokenResponse;
import com.zupu.zupu.dto.UserInfoVO;
import com.zupu.zupu.security.LoginUser;
import com.zupu.zupu.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证 REST 接口。
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public Result<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(authService.login(request));
    }

    @PostMapping("/register")
    public Result<TokenResponse> register(@Valid @RequestBody RegisterRequest request) {
        return Result.success(authService.register(request));
    }

    @PostMapping("/refresh")
    public Result<TokenResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        return Result.success(authService.refresh(request));
    }

    @PostMapping("/logout")
    public Result<Void> logout(@AuthenticationPrincipal LoginUser user) {
        authService.logout(user == null ? null : user.getUsername());
        return Result.success();
    }

    @GetMapping("/me")
    public Result<UserInfoVO> me(@AuthenticationPrincipal LoginUser user) {
        return Result.success(authService.currentUser(user));
    }
}
