package com.zupu.zupu.controller;

import com.zupu.zupu.common.PageResult;
import com.zupu.zupu.common.Result;
import com.zupu.zupu.entity.Person;
import com.zupu.zupu.model.LookupOption;
import com.zupu.zupu.security.LoginUser;
import com.zupu.zupu.service.PersonService;
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
@RequestMapping("/api/persons")
public class PersonController {
    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping
    public Result<PageResult<Person>> list(@RequestParam(required = false) Integer familyId,
                                           @RequestParam(required = false) String keyword,
                                           @RequestParam(defaultValue = "1") int pageNum,
                                           @RequestParam(defaultValue = "10") int pageSize,
                                           @AuthenticationPrincipal LoginUser user) {
        if (familyId == null) {
            return Result.success(new PageResult<>(List.of(), 0, pageNum, pageSize));
        }
        return Result.success(personService.search(familyId, keyword, pageNum, pageSize, user));
    }

    @GetMapping("/family")
    public Result<List<Person>> listByFamily(@RequestParam Integer familyId, @AuthenticationPrincipal LoginUser user) {
        return Result.success(personService.listByFamilyId(familyId, user));
    }

    @GetMapping("/parent-options")
    public Result<List<LookupOption>> parentOptions(@RequestParam Integer familyId,
                                                    @RequestParam(required = false) Integer excludeId,
                                                    @AuthenticationPrincipal LoginUser user) {
        return Result.success(personService.listParentOptions(familyId, excludeId, user));
    }

    @GetMapping("/{id}")
    public Result<Person> get(@PathVariable Integer id, @AuthenticationPrincipal LoginUser user) {
        return Result.success(personService.getAccessiblePerson(id, user));
    }

    @PostMapping
    public Result<Person> create(@Valid @RequestBody Person person, @AuthenticationPrincipal LoginUser user) {
        personService.createPerson(person, user);
        return Result.success(person);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Integer id, @Valid @RequestBody Person person, @AuthenticationPrincipal LoginUser user) {
        person.setId(id);
        personService.updatePerson(person, user);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id, @AuthenticationPrincipal LoginUser user) {
        personService.deletePerson(id, user);
        return Result.success();
    }
}
