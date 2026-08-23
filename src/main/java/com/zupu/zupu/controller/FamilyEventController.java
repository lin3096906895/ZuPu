package com.zupu.zupu.controller;

import com.zupu.zupu.common.Result;
import com.zupu.zupu.entity.FamilyEvent;
import com.zupu.zupu.security.LoginUser;
import com.zupu.zupu.service.FamilyEventService;
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
@RequestMapping("/api/events")
public class FamilyEventController {
    private final FamilyEventService familyEventService;

    public FamilyEventController(FamilyEventService familyEventService) {
        this.familyEventService = familyEventService;
    }

    @GetMapping
    public Result<List<FamilyEvent>> list(@RequestParam Integer familyId, @AuthenticationPrincipal LoginUser user) {
        return Result.success(familyEventService.listByFamilyId(familyId, user));
    }

    @PostMapping
    public Result<FamilyEvent> create(@RequestBody FamilyEvent event, @AuthenticationPrincipal LoginUser user) {
        familyEventService.create(event, user);
        return Result.success(event);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Integer id, @RequestBody FamilyEvent event, @AuthenticationPrincipal LoginUser user) {
        event.setId(id);
        familyEventService.update(event, user);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id, @AuthenticationPrincipal LoginUser user) {
        familyEventService.delete(id, user);
        return Result.success();
    }
}
