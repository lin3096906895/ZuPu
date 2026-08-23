package com.zupu.zupu.dto;

import com.zupu.zupu.security.LoginUser;
import lombok.Data;

@Data
public class UserInfoVO {
    private Integer id;
    private String username;
    private String displayName;
    private String role;
    private String avatar;

    public static UserInfoVO from(LoginUser user) {
        UserInfoVO vo = new UserInfoVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setDisplayName(user.getDisplayName());
        vo.setRole(user.getRole());
        vo.setAvatar(user.getAvatar());
        return vo;
    }
}
