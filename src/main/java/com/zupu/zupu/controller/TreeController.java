package com.zupu.zupu.controller;

import com.zupu.zupu.common.Result;
import com.zupu.zupu.dto.TreeNodeVO;
import com.zupu.zupu.model.GraphVO;
import com.zupu.zupu.security.LoginUser;
import com.zupu.zupu.service.TreeService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tree")
public class TreeController {
    private final TreeService treeService;

    public TreeController(TreeService treeService) {
        this.treeService = treeService;
    }

    @GetMapping
    public Result<GraphVO> graph(@RequestParam Integer familyId, @AuthenticationPrincipal LoginUser user) {
        return Result.success(treeService.buildGraph(familyId, user));
    }

    @GetMapping("/roots")
    public Result<List<TreeNodeVO>> roots(@RequestParam Integer familyId, @AuthenticationPrincipal LoginUser user) {
        return Result.success(treeService.getRoots(familyId, user));
    }

    @GetMapping("/children")
    public Result<List<TreeNodeVO>> children(@RequestParam Integer personId, @AuthenticationPrincipal LoginUser user) {
        return Result.success(treeService.getChildren(personId, user));
    }
}
