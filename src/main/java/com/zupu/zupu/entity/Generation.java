package com.zupu.zupu.entity;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 世代实体类
 * 表示家族中的世代信息
 */
@Data
public class Generation {
    
    private Integer id;
    
    @NotNull(message = "所属家族不能为空")
    private Integer familyId;
    
    @NotBlank(message = "世代名称不能为空")
    @Size(min = 1, max = 30, message = "世代名称长度应在1-30个字符之间")
    private String title;
    
    @NotNull(message = "世代顺序不能为空")
    @Min(value = 1, message = "世代顺序至少为1")
    private Integer sortOrder;
    
    @Size(max = 500, message = "世代描述不能超过500个字符")
    private String description;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
    
    private boolean canEdit;
}
