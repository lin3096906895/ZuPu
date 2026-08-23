package com.zupu.zupu.service;

import com.zupu.zupu.common.PageResult;
import com.zupu.zupu.entity.Person;
import com.zupu.zupu.model.LookupOption;
import com.zupu.zupu.security.LoginUser;

import java.util.List;

public interface PersonService {
    List<Person> listByFamilyId(Integer familyId, LoginUser user);

    PageResult<Person> search(Integer familyId, String keyword, int pageNum, int pageSize, LoginUser user);

    Person getAccessiblePerson(Integer id, LoginUser user);

    Person getById(Integer id);

    void createPerson(Person person, LoginUser user);

    void updatePerson(Person person, LoginUser user);

    void deletePerson(Integer id, LoginUser user);

    int countAccessiblePeople(LoginUser user);

    List<LookupOption> listParentOptions(Integer familyId, Integer excludeId, LoginUser user);
}
