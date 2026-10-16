package com.zupu.zupu;

import com.zupu.zupu.config.DemoAccountGuard;
import com.zupu.zupu.entity.User;
import com.zupu.zupu.mapper.UserMapper;
import com.zupu.zupu.util.PasswordUtils;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DemoAccountGuardTest {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private User user(String username, String passwordHash) {
        User user = new User();
        user.setId(1);
        user.setUsername(username);
        user.setPasswordHash(passwordHash);
        return user;
    }

    @Test
    void disablesDemoAccountStillUsingDefaultPassword() {
        UserMapper userMapper = mock(UserMapper.class);
        when(userMapper.selectByUsername("admin")).thenReturn(user("admin", PasswordUtils.encode("admin123456")));
        when(userMapper.selectByUsername("demo")).thenReturn(null);

        new DemoAccountGuard(userMapper, passwordEncoder, false).run(null);

        verify(userMapper).updateEnabled(1, 0);
    }

    @Test
    void keepsDemoAccountEnabledWhenFlagIsOn() {
        UserMapper userMapper = mock(UserMapper.class);
        when(userMapper.selectByUsername("admin")).thenReturn(user("admin", PasswordUtils.encode("admin123456")));
        when(userMapper.selectByUsername("demo")).thenReturn(null);

        new DemoAccountGuard(userMapper, passwordEncoder, true).run(null);

        verify(userMapper, never()).updateEnabled(anyInt(), anyInt());
    }

    @Test
    void leavesAccountsAloneOncePasswordChanged() {
        UserMapper userMapper = mock(UserMapper.class);
        when(userMapper.selectByUsername("admin"))
                .thenReturn(user("admin", passwordEncoder.encode("a-brand-new-password")));
        when(userMapper.selectByUsername("demo"))
                .thenReturn(user("demo", passwordEncoder.encode("another-new-password")));

        new DemoAccountGuard(userMapper, passwordEncoder, false).run(null);

        verify(userMapper, never()).updateEnabled(anyInt(), anyInt());
    }

    @Test
    void doesNothingWhenAccountMissing() {
        UserMapper userMapper = mock(UserMapper.class);
        when(userMapper.selectByUsername(anyString())).thenReturn(null);

        new DemoAccountGuard(userMapper, passwordEncoder, false).run(null);

        verify(userMapper, never()).updateEnabled(anyInt(), anyInt());
    }
}
