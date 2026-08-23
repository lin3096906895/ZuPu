package com.zupu.zupu.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zupu.zupu.common.BizException;
import com.zupu.zupu.common.PageResult;
import com.zupu.zupu.entity.Person;
import com.zupu.zupu.mapper.PersonMapper;
import com.zupu.zupu.model.LookupOption;
import com.zupu.zupu.security.LoginUser;
import com.zupu.zupu.service.FamilyService;
import com.zupu.zupu.service.PersonService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PersonServiceImpl implements PersonService {
    private final PersonMapper personMapper;
    private final FamilyService familyService;

    public PersonServiceImpl(PersonMapper personMapper, FamilyService familyService) {
        this.personMapper = personMapper;
        this.familyService = familyService;
    }

    @Override
    public List<Person> listByFamilyId(Integer familyId, LoginUser user) {
        familyService.getAccessibleFamily(familyId, user);
        List<Person> persons = personMapper.selectByFamilyId(familyId);
        for (Person person : persons) {
            person.setCanEdit(canEdit(person, user));
        }
        return persons;
    }

    @Override
    public PageResult<Person> search(Integer familyId, String keyword, int pageNum, int pageSize, LoginUser user) {
        if (familyId != null) {
            familyService.getAccessibleFamily(familyId, user);
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Person> persons = personMapper.selectByFilters(familyId, keyword);
        long total = new PageInfo<>(persons).getTotal();
        for (Person person : persons) {
            person.setCanEdit(canEdit(person, user));
        }
        return new PageResult<>(persons, total, pageNum, pageSize);
    }

    @Override
    public Person getAccessiblePerson(Integer id, LoginUser user) {
        Person person = personMapper.selectById(id);
        if (person == null) {
            throw new BizException(404, "人物不存在");
        }
        familyService.getAccessibleFamily(person.getFamilyId(), user);
        person.setCanEdit(canEdit(person, user));
        person.setSpouseNames(personMapper.selectSpouseNames(id));
        return person;
    }

    @Override
    public Person getById(Integer id) {
        return personMapper.selectById(id);
    }

    @Override
    @CacheEvict(value = {"familyTree", "familyStats"}, allEntries = true)
    public void createPerson(Person person, LoginUser user) {
        validatePerson(person, user);
        deriveGeneration(person);
        validateParentCycle(person);
        personMapper.insert(person);
    }

    @Override
    @CacheEvict(value = {"familyTree", "familyStats"}, allEntries = true)
    public void updatePerson(Person person, LoginUser user) {
        getAccessiblePerson(person.getId(), user);
        validatePerson(person, user);
        deriveGeneration(person);
        validateParentCycle(person);
        personMapper.update(person);
    }

    @Override
    @CacheEvict(value = {"familyTree", "familyStats"}, allEntries = true)
    public void deletePerson(Integer id, LoginUser user) {
        getAccessiblePerson(id, user);
        personMapper.deleteById(id);
    }

    @Override
    public int countAccessiblePeople(LoginUser user) {
        if (user == null) {
            return 0;
        }
        return personMapper.countAccessible(user.getId(), isAdmin(user));
    }

    @Override
    public List<LookupOption> listParentOptions(Integer familyId, Integer excludeId, LoginUser user) {
        familyService.getAccessibleFamily(familyId, user);
        return personMapper.selectParentOptions(familyId, excludeId);
    }

    private void validatePerson(Person person, LoginUser user) {
        if (person.getFamilyId() == null) {
            throw new BizException("请选择所属家族");
        }
        familyService.getAccessibleFamily(person.getFamilyId(), user);
        if (!StringUtils.hasText(person.getName())) {
            throw new BizException("人物姓名不能为空");
        }
        if (!StringUtils.hasText(person.getGender())) {
            person.setGender("男");
        }
        if (person.getStatus() == null) {
            person.setStatus(1);
        }
        if (person.getOrderNum() == null) {
            person.setOrderNum(0);
        }
        validateParentExists(person);
    }

    private void validateParentExists(Person person) {
        if (person.getFatherId() != null) {
            Person father = personMapper.selectById(person.getFatherId());
            if (father == null || !father.getFamilyId().equals(person.getFamilyId())) {
                throw new BizException("父亲必须属于同一家族");
            }
        }
        if (person.getMotherId() != null) {
            Person mother = personMapper.selectById(person.getMotherId());
            if (mother == null || !mother.getFamilyId().equals(person.getFamilyId())) {
                throw new BizException("母亲必须属于同一家族");
            }
        }
    }

    private void deriveGeneration(Person person) {
        if (person.getFatherId() != null) {
            Person father = personMapper.selectById(person.getFatherId());
            if (father != null && father.getGenerationNo() != null) {
                person.setGenerationNo(father.getGenerationNo() + 1);
                return;
            }
        }
        if (person.getGenerationNo() == null) {
            person.setGenerationNo(1);
        }
    }

    private void validateParentCycle(Person person) {
        if (person.getId() == null) {
            return;
        }
        if (person.getId().equals(person.getFatherId()) || person.getId().equals(person.getMotherId())) {
            throw new BizException("不能把自己设为父母");
        }
        checkAncestorChain(person.getFatherId(), person.getId());
        checkAncestorChain(person.getMotherId(), person.getId());
    }

    private void checkAncestorChain(Integer startId, Integer selfId) {
        Set<Integer> visited = new HashSet<>();
        Integer current = startId;
        while (current != null) {
            if (current.equals(selfId)) {
                throw new BizException("亲属关系形成循环，请重新选择父母");
            }
            if (!visited.add(current)) {
                break;
            }
            Person parent = personMapper.selectById(current);
            current = parent == null ? null : parent.getFatherId();
        }
    }

    private boolean canEdit(Person person, LoginUser user) {
        if (user == null) {
            return false;
        }
        return isAdmin(user) || familyService.getById(person.getFamilyId()).getOwnerUserId().equals(user.getId());
    }

    private boolean isAdmin(LoginUser user) {
        return user != null && "ADMIN".equalsIgnoreCase(user.getRole());
    }
}
