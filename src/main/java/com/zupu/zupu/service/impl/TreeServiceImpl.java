package com.zupu.zupu.service.impl;

import com.zupu.zupu.common.BizException;
import com.zupu.zupu.dto.TreeNodeVO;
import com.zupu.zupu.entity.Marriage;
import com.zupu.zupu.entity.Person;
import com.zupu.zupu.mapper.MarriageMapper;
import com.zupu.zupu.mapper.PersonMapper;
import com.zupu.zupu.model.GraphVO;
import com.zupu.zupu.security.LoginUser;
import com.zupu.zupu.service.FamilyService;
import com.zupu.zupu.service.TreeService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class TreeServiceImpl implements TreeService {
    private final PersonMapper personMapper;
    private final MarriageMapper marriageMapper;
    private final FamilyService familyService;

    public TreeServiceImpl(PersonMapper personMapper, MarriageMapper marriageMapper, FamilyService familyService) {
        this.personMapper = personMapper;
        this.marriageMapper = marriageMapper;
        this.familyService = familyService;
    }

    @Override
    @Cacheable(value = "familyTree", key = "#familyId")
    public GraphVO buildGraph(Integer familyId, LoginUser user) {
        familyService.getAccessibleFamily(familyId, user);
        List<Person> people = personMapper.selectByFamilyId(familyId);
        List<Marriage> marriages = marriageMapper.selectByFamilyId(familyId);

        Map<Integer, List<String>> spouseMap = new HashMap<>();
        for (Marriage m : marriages) {
            spouseMap.computeIfAbsent(m.getHusbandId(), k -> new ArrayList<>()).add(m.getWifeName());
            spouseMap.computeIfAbsent(m.getWifeId(), k -> new ArrayList<>()).add(m.getHusbandName());
        }

        GraphVO graph = new GraphVO();
        for (Person p : people) {
            GraphVO.Node node = new GraphVO.Node();
            node.setId(p.getId());
            node.setName(p.getName());
            node.setGender(p.getGender());
            node.setGenerationNo(p.getGenerationNo());
            node.setFatherId(p.getFatherId());
            node.setMotherId(p.getMotherId());
            node.setBirthDate(p.getBirthDate());
            node.setDeathDate(p.getDeathDate());
            node.setSpouseNames(spouseMap.get(p.getId()));
            graph.getNodes().add(node);

            if (p.getFatherId() != null) {
                graph.getEdges().add(new GraphVO.Edge(p.getFatherId(), p.getId(), "FATHER"));
            }
            if (p.getMotherId() != null) {
                graph.getEdges().add(new GraphVO.Edge(p.getMotherId(), p.getId(), "MOTHER"));
            }
        }
        for (Marriage m : marriages) {
            graph.getEdges().add(new GraphVO.Edge(m.getHusbandId(), m.getWifeId(), "MARRIAGE"));
        }
        return graph;
    }

    @Override
    public List<TreeNodeVO> getRoots(Integer familyId, LoginUser user) {
        familyService.getAccessibleFamily(familyId, user);
        List<Person> roots = personMapper.selectRoots(familyId);
        List<Marriage> marriages = marriageMapper.selectByFamilyId(familyId);
        Set<Integer> wifeIds = new HashSet<>();
        for (Marriage m : marriages) {
            wifeIds.add(m.getWifeId());
        }
        return roots.stream()
                .filter(p -> !wifeIds.contains(p.getId()))
                .map(this::toNode)
                .toList();
    }

    @Override
    public List<TreeNodeVO> getChildren(Integer personId, LoginUser user) {
        Person person = personMapper.selectById(personId);
        if (person == null) {
            throw new BizException(404, "人物不存在");
        }
        familyService.getAccessibleFamily(person.getFamilyId(), user);
        return personMapper.selectChildren(personId).stream().map(this::toNode).toList();
    }

    private TreeNodeVO toNode(Person p) {
        List<String> spouses = personMapper.selectSpouseNames(p.getId());
        boolean hasChildren = !personMapper.selectChildren(p.getId()).isEmpty();
        return new TreeNodeVO(p.getId(), p.getName(), p.getGender(), p.getGenerationNo(),
                p.getBirthDate(), p.getDeathDate(), spouses, hasChildren);
    }
}
