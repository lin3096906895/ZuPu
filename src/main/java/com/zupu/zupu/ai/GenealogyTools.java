package com.zupu.zupu.ai;

import com.zupu.zupu.dto.PersonBrief;
import com.zupu.zupu.dto.RelationshipVO;
import com.zupu.zupu.entity.Person;
import com.zupu.zupu.mapper.PersonMapper;
import com.zupu.zupu.service.RelationshipService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 供 AI Function Calling 使用的族谱查询工具。
 */
@Component
public class GenealogyTools {
    private final PersonMapper personMapper;
    private final RelationshipService relationshipService;

    public GenealogyTools(PersonMapper personMapper, RelationshipService relationshipService) {
        this.personMapper = personMapper;
        this.relationshipService = relationshipService;
    }

    @Tool(description = "根据姓名模糊查询家族成员")
    public List<PersonBrief> searchPerson(@ToolParam(description = "姓名关键词") String name) {
        return personMapper.selectByFilters(null, name).stream().map(this::toBrief).toList();
    }

    @Tool(description = "查询某人的子女")
    public List<PersonBrief> getChildren(@ToolParam(description = "人物ID") Integer personId) {
        return personMapper.selectChildren(personId).stream().map(this::toBrief).toList();
    }

    @Tool(description = "查询某人的父母信息")
    public PersonBrief getParents(@ToolParam(description = "人物ID") Integer personId) {
        Person p = personMapper.selectById(personId);
        if (p == null) {
            return null;
        }
        return toBrief(p);
    }

    @Tool(description = "查询两个人的亲缘关系（按姓名，需在同一家族）")
    public String getRelationship(@ToolParam(description = "第一个人姓名") String nameA,
                                  @ToolParam(description = "第二个人姓名") String nameB,
                                  @ToolParam(description = "家族ID") Integer familyId) {
        Person a = findByName(nameA, familyId);
        Person b = findByName(nameB, familyId);
        if (a == null || b == null) {
            return "未找到对应人物，请确认姓名是否正确";
        }
        RelationshipVO vo = relationshipService.compute(a.getId(), b.getId(), familyId);
        return vo.description() + "（血缘路径：" + String.join(" → ", vo.path()) + "）";
    }

    private Person findByName(String name, Integer familyId) {
        List<Person> list = personMapper.selectByFilters(familyId, name);
        return list.isEmpty() ? null : list.get(0);
    }

    private PersonBrief toBrief(Person p) {
        return new PersonBrief(p.getId(), p.getName(), p.getGender(), p.getGenerationNo(),
                p.getFatherName(), p.getMotherName(), personMapper.selectSpouseNames(p.getId()));
    }
}
