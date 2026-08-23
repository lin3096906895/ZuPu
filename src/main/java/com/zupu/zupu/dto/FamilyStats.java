package com.zupu.zupu.dto;

import java.io.Serializable;
import java.util.List;

/**
 * 家族统计信息。
 */
public record FamilyStats(
        String familyName,
        int total,
        int male,
        int female,
        List<GenerationStat> generations
) implements Serializable {
    public record GenerationStat(int generationNo, int count) implements Serializable {
    }
}
