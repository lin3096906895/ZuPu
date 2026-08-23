package com.zupu.zupu.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/file")
public class FileController {
    
    // 上传文件保存路径，从配置文件读取，默认 uploads/
    @Value("${app.upload.path:uploads/}")
    private String uploadPath;
    
    /**
     * 文件上传接口
     * 接收前端上传的文件，保存到服务器并返回文件访问路径
     * 
     * @param file 前端表单提交的文件（字段名必须为 "file"）
     * @return 包含上传结果的 Map（success、filename、originalFilename 等）
     */
    @PostMapping("/upload")
    public Map<String, Object> upload(@RequestParam("file") MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        
        // 1. 检查文件是否为空
        if (file.isEmpty()) {
            result.put("success", false);
            result.put("message", "请选择要上传的文件");
            return result;
        }
        
        try {
            // 2. 按日期创建上传目录：uploads/2026/05/27/
            String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            Path uploadDir = Paths.get(uploadPath, datePath);
            Files.createDirectories(uploadDir);
            
            // 3. 获取原始文件名和扩展名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            
            // 4. 生成新文件名（UUID 避免文件名冲突）
            String newFilename = UUID.randomUUID().toString() + extension;
            
            // 5. 保存文件到磁盘
            Path filePath = uploadDir.resolve(newFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            // 6. 构建文件访问路径
            String encodedFilename = java.net.URLEncoder.encode(originalFilename, StandardCharsets.UTF_8);
            String fileUrl = "/api/file/download/" + datePath + "/" + newFilename + "?originalFilename=" + encodedFilename;
            
            // 7. 返回成功结果
            result.put("success", true);
            result.put("message", "上传成功");
            result.put("url", fileUrl);  // 完整的访问URL
            result.put("filename", datePath + "/" + newFilename);  // 相对路径
            result.put("originalFilename", originalFilename);  // 原始文件名
            result.put("size", file.getSize());  // 文件大小
            
        } catch (IOException e) {
            // 8. 处理上传失败
            result.put("success", false);
            result.put("message", "上传失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 文件下载接口
     * 根据文件路径读取文件并返回给浏览器下载
     * 
     * @param request HTTP 请求对象，从中提取文件路径
     * @param originalFilename 原始文件名（可选，用于设置下载时的文件名）
     * @return 文件资源
     */
    @GetMapping("/download/**")
    public ResponseEntity<Resource> download(HttpServletRequest request,
                                            @RequestParam(required = false) String originalFilename) {
        try {
            // 1. 从请求 URI 中提取文件路径
            // 例如：/api/file/download/2026/05/27/abc.jpg -> 2026/05/27/abc.jpg
            String uri = request.getRequestURI();
            String filePath = uri.replace("/api/file/download/", "");
            
            // 2. 处理 URL 中可能携带的参数（如 ?originalFilename=xxx.jpg）
            if (filePath.contains("?")) {
                filePath = filePath.substring(0, filePath.indexOf("?"));
            }
            
            // 3. 拼接完整文件路径
            Path file = Paths.get(uploadPath, filePath);
            
            // 4. 检查文件是否存在
            if (!Files.exists(file)) {
                return ResponseEntity.notFound().build();  // 返回 404
            }
            
            // 5. 创建文件资源
            Resource resource = new UrlResource(file.toUri());
            
            // 6. 设置下载文件名
            String downloadFilename = originalFilename != null ? originalFilename : file.getFileName().toString();
            
            // 7. 返回文件响应
            return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)  // 二进制流类型
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                        "attachment; filename=\"" + java.net.URLEncoder.encode(downloadFilename, StandardCharsets.UTF_8) + "\"")  // 下载时的文件名
                .body(resource);  // 文件内容
                
        } catch (MalformedURLException e) {
            return ResponseEntity.internalServerError().build();  // 返回 500
        }
    }
}
