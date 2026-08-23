package com.zupu.zupu.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DashboardSummary {
    private int familyCount;
    private int generationCount;
    private int personCount;
    private int userCount;
}
