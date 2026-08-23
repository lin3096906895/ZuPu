package com.zupu.zupu.entity;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 人物实体：双亲 + 承祧父母 + 世代深度（图模型）。
 */
@Data
public class Person {

    private Integer id;

    @NotNull(message = "所属家族不能为空")
    private Integer familyId;

    @NotBlank(message = "姓名不能为空")
    @Size(min = 2, max = 20, message = "姓名长度应在2-20个字符之间")
    private String name;

    @Pattern(regexp = "^(男|女)$", message = "性别必须是男或女")
    private String gender;

    @Past(message = "出生日期必须是过去的时间")
    private LocalDate birthDate;

    @Past(message = "逝世日期必须是过去的时间")
    private LocalDate deathDate;

    @Size(max = 100, message = "出生地不能超过100个字符")
    private String birthPlace;

    @Size(max = 2000, message = "人物简介不能超过2000个字符")
    private String biography;

    private String photo;

    private Integer fatherId;

    private Integer motherId;

    private Integer adoptiveFatherId;

    private Integer adoptiveMotherId;

    @Min(value = 1, message = "世代至少为1")
    private Integer generationNo;

    private Integer orderNum;

    @NotNull(message = "状态不能为空")
    @Min(value = 0, message = "状态值无效")
    @Max(value = 1, message = "状态值无效")
    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    // 非数据库展示字段
    private String familyName;
    private String fatherName;
    private String motherName;
    private List<String> spouseNames;
    private boolean canEdit;
}
