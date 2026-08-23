package com.zupu.zupu.controller;

import com.zupu.zupu.model.LookupOption;
import com.zupu.zupu.security.LoginUser;
import com.zupu.zupu.service.GenerationService;
import com.zupu.zupu.service.PersonService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/lookups")
public class LookupController {
    private final GenerationService generationService;
    private final PersonService personService;

    public LookupController(GenerationService generationService, PersonService personService) {
        this.generationService = generationService;
        this.personService = personService;
    }

    @GetMapping("/families/{familyId}/generations")
    public List<LookupOption> generationOptions(@PathVariable Integer familyId, @AuthenticationPrincipal LoginUser user) {
        return generationService.listOptionsByFamilyId(familyId, user);
    }

    @GetMapping("/families/{familyId}/parents")
    public List<LookupOption> parentOptions(@PathVariable Integer familyId,
                                            @RequestParam(required = false) Integer excludeId,
                                            @AuthenticationPrincipal LoginUser user) {
        return personService.listParentOptions(familyId, excludeId, user);
    }
}
