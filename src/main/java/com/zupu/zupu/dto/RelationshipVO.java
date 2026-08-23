package com.zupu.zupu.dto;

import java.util.List;

/**
 * 两人亲缘关系查询结果。
 */
public record RelationshipVO(
        String personA,
        String personB,
        String term,
        String description,
        List<String> path
) {
}
