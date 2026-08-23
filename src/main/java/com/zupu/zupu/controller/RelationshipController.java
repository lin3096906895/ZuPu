package com.zupu.zupu.controller;

import com.zupu.zupu.common.Result;
import com.zupu.zupu.dto.RelationshipVO;
import com.zupu.zupu.security.LoginUser;
import com.zupu.zupu.service.RelationshipService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/relationship")
public class RelationshipController {
    private final RelationshipService relationshipService;

    public RelationshipController(RelationshipService relationshipService) {
        this.relationshipService = relationshipService;
    }

    @GetMapping
    public Result<RelationshipVO> relationship(@RequestParam Integer aId,
                                               @RequestParam Integer bId,
                                               @RequestParam Integer familyId,
                                               @AuthenticationPrincipal LoginUser user) {
        return Result.success(relationshipService.getRelationship(aId, bId, familyId, user));
    }
}
