package com.zupu.zupu.entity;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 家族实体类
 * 表示家族信息
 */
@Data
public class Family {
    
    private Integer id;
    
    @NotBlank(message = "家族名称不能为空")
    @Size(min = 2, max = 50, message = "家族名称长度应在2-50个字符之间")
    private String name;
    
    @NotBlank(message = "始祖名称不能为空")
    @Size(min = 1, max = 30, message = "始祖名称长度应在1-30个字符之间")
    private String ancestorName;
    
    @Size(max = 2000, message = "家族简介不能超过2000个字符")
    private String description;
    
    private Integer ownerUserId;
    
    @NotNull(message = "状态不能为空")
    @Min(value = 0, message = "状态值无效")
    @Max(value = 1, message = "状态值无效")
    private Integer status;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
    
    private String ownerName;
    
    private boolean canEdit;
}
