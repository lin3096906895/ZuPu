package com.zupu.zupu.controller;

import com.zupu.zupu.common.Result;
import com.zupu.zupu.entity.Marriage;
import com.zupu.zupu.security.LoginUser;
import com.zupu.zupu.service.MarriageService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/marriages")
public class MarriageController {
    private final MarriageService marriageService;

    public MarriageController(MarriageService marriageService) {
        this.marriageService = marriageService;
    }

    @GetMapping
    public Result<List<Marriage>> list(@RequestParam Integer familyId, @AuthenticationPrincipal LoginUser user) {
        return Result.success(marriageService.listByFamilyId(familyId, user));
    }

    @GetMapping("/person/{personId}")
    public Result<List<Marriage>> listByPerson(@PathVariable Integer personId, @AuthenticationPrincipal LoginUser user) {
        return Result.success(marriageService.listByPersonId(personId, user));
    }

    @PostMapping
    public Result<Marriage> create(@RequestBody Marriage marriage, @AuthenticationPrincipal LoginUser user) {
        marriageService.create(marriage, user);
        return Result.success(marriage);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Integer id, @RequestBody Marriage marriage, @AuthenticationPrincipal LoginUser user) {
        marriage.setId(id);
        marriageService.update(marriage, user);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id, @AuthenticationPrincipal LoginUser user) {
        marriageService.delete(id, user);
        return Result.success();
    }
}
