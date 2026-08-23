package com.zupu.zupu.dto;

import java.util.List;

/**
 * 人物简要信息，供 AI 工具返回。
 */
public record PersonBrief(Integer id, String name, String gender, Integer generationNo,
                          String fatherName, String motherName, List<String> spouseNames) {
}
