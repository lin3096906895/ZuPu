package com.zupu.zupu.controller;

import com.zupu.zupu.common.BizException;
import com.zupu.zupu.common.Result;
import com.zupu.zupu.dto.UserInfoVO;
import com.zupu.zupu.security.LoginUser;
import com.zupu.zupu.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    @Value("${app.upload.path:uploads/}")
    private String uploadPath;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public Result<UserInfoVO> me(@AuthenticationPrincipal LoginUser user) {
        return Result.success(UserInfoVO.from(user));
    }

    @PutMapping("/display-name")
    public Result<Void> updateDisplayName(@RequestParam String displayName, @AuthenticationPrincipal LoginUser user) {
        userService.updateDisplayName(user.getId(), displayName, user);
        return Result.success();
    }

    @PutMapping("/password")
    public Result<Void> changePassword(@RequestParam String oldPassword,
                                       @RequestParam String newPassword,
                                       @RequestParam String confirmPassword,
                                       @AuthenticationPrincipal LoginUser user) {
        if (!newPassword.equals(confirmPassword)) {
            throw new BizException("两次输入的密码不一致");
        }
        userService.updatePassword(user.getId(), oldPassword, newPassword, user);
        return Result.success();
    }

    @PutMapping("/avatar")
    public Result<String> updateAvatar(@RequestParam("file") MultipartFile file, @AuthenticationPrincipal LoginUser user) {
        if (file.isEmpty()) {
            throw new BizException("请选择要上传的头像文件");
        }
        if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
            throw new BizException("请上传图片文件");
        }
        try {
            Path dir = Paths.get(uploadPath, "avatars");
            Files.createDirectories(dir);
            String original = file.getOriginalFilename();
            String ext = "";
            if (original != null && original.contains(".")) {
                ext = original.substring(original.lastIndexOf("."));
            }
            String filename = "avatar_" + user.getId() + "_" + UUID.randomUUID() + ext;
            file.transferTo(dir.resolve(filename).toAbsolutePath());
            String avatarUrl = "/api/file/download/avatars/" + filename;
            userService.updateAvatar(user.getId(), avatarUrl, user);
            return Result.success(avatarUrl);
        } catch (IOException e) {
            throw new BizException("头像上传失败：" + e.getMessage());
        }
    }
}
