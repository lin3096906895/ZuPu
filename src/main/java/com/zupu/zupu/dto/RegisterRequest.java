package com.zupu.zupu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度 3-50")
    private String username;

    @NotBlank(message = "昵称不能为空")
    @Size(max = 50, message = "昵称长度不超过 50")
    private String displayName;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 100, message = "密码长度 6-100")
    private String password;
}
