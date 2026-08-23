package com.zupu.zupu.dto;

import lombok.Data;

@Data
public class TokenResponse {
    private String accessToken;
    private String refreshToken;
    private long expiresIn;
    private UserInfoVO user;
}
