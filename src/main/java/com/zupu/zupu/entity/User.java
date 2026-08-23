package com.zupu.zupu.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
    private Integer id;
    private String username;
    private String passwordHash;
    private String displayName;
    private String role;
    private Integer enabled;
    private String avatar;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
