package com.zupu.zupu.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * 家族树节点（用于懒加载，带 hasChildren 标记）。
 */
public record TreeNodeVO(
        Integer id,
        String name,
        String gender,
        Integer generationNo,
        LocalDate birthDate,
        LocalDate deathDate,
        List<String> spouseNames,
        boolean hasChildren
) {
}
