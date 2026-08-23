package com.zupu.zupu.service;

import com.zupu.zupu.dto.RelationshipVO;
import com.zupu.zupu.security.LoginUser;

public interface RelationshipService {
    RelationshipVO getRelationship(Integer aId, Integer bId, Integer familyId, LoginUser user);

    /**
     * 无鉴权的核心计算（供 AI 工具等内部调用，调用方需确保已鉴权）。
     */
    RelationshipVO compute(Integer aId, Integer bId, Integer familyId);
}
