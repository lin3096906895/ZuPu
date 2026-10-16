package com.zupu.zupu.config;

import com.zupu.zupu.entity.User;
import com.zupu.zupu.mapper.UserMapper;
import com.zupu.zupu.util.PasswordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 内置演示账号（admin / demo）防护。
 *
 * <p>这两个账号由 Flyway 种子数据创建，密码是写在 README 里的公开默认值。
 * 若把项目部署到公网又没改密码，等同于把管理员权限直接交出去。
 *
 * <ul>
 *   <li>{@code app.security.demo-accounts-enabled=false}（生产环境应如此）：
 *       启动时自动禁用仍在使用默认密码的演示账号。</li>
 *   <li>保持 {@code true}：演示账号可正常登录，但每次启动都会打印醒目告警。</li>
 * </ul>
 */
@Component
public class DemoAccountGuard implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(DemoAccountGuard.class);

    /** 种子数据中的演示账号，见 db/migration/V1__init.sql */
    private static final List<String> DEMO_USERNAMES = List.of("admin", "demo");
    /** README 中公开的默认密码 */
    private static final String DEFAULT_PASSWORD = "admin123456";

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final boolean demoAccountsEnabled;

    public DemoAccountGuard(UserMapper userMapper, PasswordEncoder passwordEncoder,
                            @Value("${app.security.demo-accounts-enabled:true}") boolean demoAccountsEnabled) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.demoAccountsEnabled = demoAccountsEnabled;
    }

    @Override
    public void run(ApplicationArguments args) {
        for (String username : DEMO_USERNAMES) {
            User user = userMapper.selectByUsername(username);
            if (user == null || !usesDefaultPassword(user)) {
                // 账号不存在，或密码已被修改过，无需干预
                continue;
            }
            if (demoAccountsEnabled) {
                log.warn("演示账号 [{}] 仍在使用 README 中公开的默认密码，请尽快修改。"
                        + "生产环境请设置环境变量 DEMO_ACCOUNTS_ENABLED=false 以自动禁用该账号。", username);
            } else {
                userMapper.updateEnabled(user.getId(), 0);
                log.warn("已禁用仍在使用默认密码的演示账号 [{}]。"
                        + "如需重新启用，请在数据库中将 users.enabled 置回 1 并立即修改密码。", username);
            }
        }
    }

    /** 兼容种子数据的 SHA-256 哈希，以及登录后惰性迁移产生的 BCrypt 哈希 */
    private boolean usesDefaultPassword(User user) {
        String hash = user.getPasswordHash();
        if (hash == null) {
            return false;
        }
        if (hash.startsWith("$2")) {
            return passwordEncoder.matches(DEFAULT_PASSWORD, hash);
        }
        return PasswordUtils.matches(DEFAULT_PASSWORD, hash);
    }
}
