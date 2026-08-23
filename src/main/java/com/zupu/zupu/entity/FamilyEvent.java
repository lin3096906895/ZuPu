package com.zupu.zupu.entity;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 家族事件实体类
 * 表示家族中的事件信息
 */
@Data
public class FamilyEvent {
    /**
     * 事件ID
     */
    private Integer id;
    
    /**
     * 所属家族ID
     */
    private Integer familyId;
    
    /**
     * 事件标题
     */
    private String title;
    
    /**
     * 事件日期
     */
    private LocalDate eventDate;
    
    /**
     * 事件描述
     */
    private String description;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 是否可编辑（非数据库字段，用于前端权限控制）
     */
    private boolean canEdit;
}
