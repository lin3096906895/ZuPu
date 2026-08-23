package com.zupu.zupu.dto;

import lombok.Data;

@Data
public class ChatRequest {
    private String question;
    private Integer familyId;
    private String sessionId;
}
