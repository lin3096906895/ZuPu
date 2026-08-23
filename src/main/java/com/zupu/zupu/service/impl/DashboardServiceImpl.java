package com.zupu.zupu.service.impl;

import com.zupu.zupu.model.DashboardSummary;
import com.zupu.zupu.security.LoginUser;
import com.zupu.zupu.service.DashboardService;
import com.zupu.zupu.service.FamilyService;
import com.zupu.zupu.service.GenerationService;
import com.zupu.zupu.service.PersonService;
import com.zupu.zupu.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class DashboardServiceImpl implements DashboardService {
    private final FamilyService familyService;
    private final GenerationService generationService;
    private final PersonService personService;
    private final UserService userService;

    public DashboardServiceImpl(FamilyService familyService, GenerationService generationService, PersonService personService, UserService userService) {
        this.familyService = familyService;
        this.generationService = generationService;
        this.personService = personService;
        this.userService = userService;
    }

    @Override
    public DashboardSummary getSummary(LoginUser user) {
        return new DashboardSummary(
                familyService.countAccessibleFamilies(user),
                generationService.countAccessibleGenerations(user),
                personService.countAccessiblePeople(user),
                isAdmin(user) ? userService.countUsers() : 0
        );
    }

    private boolean isAdmin(LoginUser user) {
        return user != null && "ADMIN".equalsIgnoreCase(user.getRole());
    }
}
