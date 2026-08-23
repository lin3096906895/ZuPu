package com.zupu.zupu.entity;

import jakarta.validation.constraints.NotBlank;  // 非空验证注解
import jakarta.validation.constraints.Size;     // 长度验证注解
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 留言板实体类
 * 使用 JSR-303 数据验证注解定义验证规则
 */
@Data
public class MessageBoard {
    private Integer id;
    
    // 标题：非空，长度2-100字符
    @NotBlank(message = "标题不能为空")
    @Size(min = 2, max = 100, message = "标题长度应在2-100个字符之间")
    private String title;
    
    // 内容：非空，长度10-5000字符
    @NotBlank(message = "内容不能为空")
    @Size(min = 10, max = 5000, message = "内容长度应在10-5000个字符之间")
    private String content;
    
    // 分类：非空，最大20字符
    @NotBlank(message = "分类不能为空")
    @Size(max = 20, message = "分类名称不能超过20个字符")
    private String category;
    
    private String attachment;
    private String attachmentName;
    private Integer authorId;
    private String authorName;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer status;
}
