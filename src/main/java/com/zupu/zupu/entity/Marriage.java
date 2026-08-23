package com.zupu.zupu.entity;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 婚姻关系实体：支持多配偶/续弦。
 */
@Data
public class Marriage {
    private Integer id;
    private Integer familyId;
    private Integer husbandId;
    private Integer wifeId;
    private Integer marriageOrder;
    private LocalDate marryDate;
    private LocalDate divorceDate;
    private String status;
    private Integer isPrimary;
    private String note;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 非数据库展示字段
    private String husbandName;
    private String wifeName;
}
