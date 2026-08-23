package com.zupu.zupu;

import com.zupu.zupu.util.PasswordUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordUtilsTests {
    @Test
    void shouldMatchSamePassword() {
        String encoded = PasswordUtils.encode("admin123456");
        assertTrue(PasswordUtils.matches("admin123456", encoded));
        assertFalse(PasswordUtils.matches("wrong-password", encoded));
    }
}
