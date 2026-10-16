package com.zupu.zupu.service;

import com.zupu.zupu.ai.GenealogyTools;
import com.zupu.zupu.entity.Family;
import com.zupu.zupu.entity.Person;
import com.zupu.zupu.mapper.FamilyMapper;
import com.zupu.zupu.mapper.PersonMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * AI 助手：RAG 注入族谱数据 + Function Calling 查库 + 流式输出。
 */
@Service
public class AiService {
    private final ChatClient chatClient;
    private final GenealogyTools tools;
    private final PersonMapper personMapper;
    private final FamilyMapper familyMapper;
    private final int maxPromptMembers;

    public AiService(ChatClient.Builder builder, GenealogyTools tools,
                     PersonMapper personMapper, FamilyMapper familyMapper,
                     @Value("${app.ai.max-prompt-members:200}") int maxPromptMembers) {
        this.chatClient = builder.build();
        this.tools = tools;
        this.personMapper = personMapper;
        this.familyMapper = familyMapper;
        this.maxPromptMembers = maxPromptMembers;
    }

    public Flux<String> chat(String question, Integer familyId) {
        return chatClient.prompt()
                .system(buildSystemPrompt(familyId))
                .user(question)
                .tools(tools)
                .stream()
                .content();
    }

    private String buildSystemPrompt(Integer familyId) {
        StringBuilder sb = new StringBuilder();
        sb.append("你是族谱管理系统的智能助手，擅长回答家族关系、人物辈分等问题。\n");
        sb.append("回答要简洁准确，涉及人物关系时优先调用工具查询真实数据。\n");
        if (familyId != null) {
            Family family = familyMapper.selectById(familyId);
            if (family != null) {
                sb.append("\n当前家族：").append(family.getName())
                        .append("（始祖 ").append(family.getAncestorName())
                        .append("，家族ID=").append(familyId).append("）\n");
                sb.append("查询两人亲缘关系时，请调用 getRelationship 工具，传入双方姓名和家族ID。\n");
                List<Person> people = personMapper.selectByFamilyId(familyId);
                List<Person> included = people.size() > maxPromptMembers
                        ? people.subList(0, maxPromptMembers)
                        : people;
                sb.append("家族成员共 ").append(people.size()).append(" 人");
                if (included.size() < people.size()) {
                    // 成员过多时不全部注入，避免 prompt 超长导致成本与延迟失控
                    sb.append("，以下仅列出前 ").append(included.size())
                            .append(" 人；需要查询其他成员时请调用 searchPerson 等工具");
                }
                sb.append("：\n");
                for (Person p : included) {
                    sb.append("- ").append(p.getName())
                            .append("（").append(p.getGender())
                            .append("，第").append(p.getGenerationNo()).append("世");
                    if (p.getFatherName() != null) {
                        sb.append("，父 ").append(p.getFatherName());
                    }
                    if (p.getMotherName() != null) {
                        sb.append("，母 ").append(p.getMotherName());
                    }
                    sb.append("）\n");
                }
            }
        }
        return sb.toString();
    }
}
