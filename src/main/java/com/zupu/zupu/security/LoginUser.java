package com.zupu.zupu.security;

import com.zupu.zupu.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * 当前登录用户，实现 UserDetails 以便 Spring Security 使用。
 */
@Getter
public class LoginUser implements UserDetails {
    private final Integer id;
    private final String username;
    private final String password;
    private final String displayName;
    private final String role;
    private final String avatar;
    private final Integer enabled;

    public LoginUser(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPasswordHash();
        this.displayName = user.getDisplayName();
        this.role = user.getRole();
        this.avatar = user.getAvatar();
        this.enabled = user.getEnabled();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled != null && enabled == 1;
    }
}
