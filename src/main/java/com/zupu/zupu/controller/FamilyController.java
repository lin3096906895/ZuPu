package com.zupu.zupu.controller;

import com.zupu.zupu.common.Result;
import com.zupu.zupu.entity.Family;
import com.zupu.zupu.security.LoginUser;
import com.zupu.zupu.service.FamilyService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/families")
public class FamilyController {
    private final FamilyService familyService;

    public FamilyController(FamilyService familyService) {
        this.familyService = familyService;
    }

    @GetMapping
    public Result<List<Family>> list(@AuthenticationPrincipal LoginUser user) {
        return Result.success(familyService.listAccessibleFamilies(user));
    }

    @GetMapping("/{id}")
    public Result<Family> get(@PathVariable Integer id, @AuthenticationPrincipal LoginUser user) {
        return Result.success(familyService.getAccessibleFamily(id, user));
    }

    @PostMapping
    public Result<Family> create(@Valid @RequestBody Family family, @AuthenticationPrincipal LoginUser user) {
        familyService.createFamily(family, user);
        return Result.success(family);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Integer id, @Valid @RequestBody Family family, @AuthenticationPrincipal LoginUser user) {
        family.setId(id);
        familyService.updateFamily(family, user);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id, @AuthenticationPrincipal LoginUser user) {
        familyService.deleteFamily(id, user);
        return Result.success();
    }
}
