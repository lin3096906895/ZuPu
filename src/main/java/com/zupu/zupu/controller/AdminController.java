package com.zupu.zupu.controller;

import com.zupu.zupu.common.Result;
import com.zupu.zupu.entity.User;
import com.zupu.zupu.security.LoginUser;
import com.zupu.zupu.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public Result<List<User>> users() {
        return Result.success(userService.listUsers());
    }

    @PutMapping("/users/{id}/role")
    public Result<Void> updateRole(@PathVariable Integer id, @RequestParam String role, @AuthenticationPrincipal LoginUser user) {
        userService.updateRole(id, role, user);
        return Result.success();
    }

    @PutMapping("/users/{id}/toggle")
    public Result<Void> toggleEnabled(@PathVariable Integer id, @AuthenticationPrincipal LoginUser user) {
        userService.toggleEnabled(id, user);
        return Result.success();
    }
}
