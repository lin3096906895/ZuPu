package com.zupu.zupu.controller;

import com.zupu.zupu.common.Result;
import com.zupu.zupu.entity.Generation;
import com.zupu.zupu.security.LoginUser;
import com.zupu.zupu.service.GenerationService;
import jakarta.validation.Valid;
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
@RequestMapping("/api/generations")
public class GenerationController {
    private final GenerationService generationService;

    public GenerationController(GenerationService generationService) {
        this.generationService = generationService;
    }

    @GetMapping
    public Result<List<Generation>> list(@RequestParam Integer familyId, @AuthenticationPrincipal LoginUser user) {
        return Result.success(generationService.listByFamilyId(familyId, user));
    }

    @PostMapping
    public Result<Generation> create(@Valid @RequestBody Generation generation, @AuthenticationPrincipal LoginUser user) {
        generationService.createGeneration(generation, user);
        return Result.success(generation);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Integer id, @Valid @RequestBody Generation generation, @AuthenticationPrincipal LoginUser user) {
        generation.setId(id);
        generationService.updateGeneration(generation, user);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id, @AuthenticationPrincipal LoginUser user) {
        generationService.deleteGeneration(id, user);
        return Result.success();
    }
}
