package com.zupu.zupu.service.impl;

import com.zupu.zupu.common.BizException;
import com.zupu.zupu.dto.RelationshipVO;
import com.zupu.zupu.entity.Marriage;
import com.zupu.zupu.entity.Person;
import com.zupu.zupu.mapper.MarriageMapper;
import com.zupu.zupu.mapper.PersonMapper;
import com.zupu.zupu.security.LoginUser;
import com.zupu.zupu.service.FamilyService;
import com.zupu.zupu.service.RelationshipService;
import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 亲缘关系查询：BFS 求最近公共祖先（LCA）+ 称谓规则表。
 */
@Service
public class RelationshipServiceImpl implements RelationshipService {
    private final PersonMapper personMapper;
    private final FamilyService familyService;
    private final MarriageMapper marriageMapper;

    public RelationshipServiceImpl(PersonMapper personMapper, FamilyService familyService, MarriageMapper marriageMapper) {
        this.personMapper = personMapper;
        this.familyService = familyService;
        this.marriageMapper = marriageMapper;
    }

    @Override
    public RelationshipVO getRelationship(Integer aId, Integer bId, Integer familyId, LoginUser user) {
        familyService.getAccessibleFamily(familyId, user);
        return compute(aId, bId, familyId);
    }

    @Override
    public RelationshipVO compute(Integer aId, Integer bId, Integer familyId) {
        List<Person> people = personMapper.selectByFamilyId(familyId);

        Map<Integer, Person> byId = new HashMap<>();
        Map<Integer, Integer> fatherOf = new HashMap<>();
        Map<Integer, Integer> motherOf = new HashMap<>();
        for (Person p : people) {
            byId.put(p.getId(), p);
            if (p.getFatherId() != null) fatherOf.put(p.getId(), p.getFatherId());
            if (p.getMotherId() != null) motherOf.put(p.getId(), p.getMotherId());
        }
        Map<Integer, Integer> spouseOf = new HashMap<>();
        for (Marriage m : marriageMapper.selectByFamilyId(familyId)) {
            spouseOf.put(m.getHusbandId(), m.getWifeId());
            spouseOf.put(m.getWifeId(), m.getHusbandId());
        }

        Person a = byId.get(aId);
        Person b = byId.get(bId);
        if (a == null || b == null) {
            throw new BizException(404, "人物不存在");
        }

        Map<Integer, Integer> towardA = new HashMap<>();
        Map<Integer, Integer> depthA = ancestorDepth(aId, fatherOf, motherOf, towardA);
        Map<Integer, Integer> towardB = new HashMap<>();
        Map<Integer, Integer> depthB = ancestorDepth(bId, fatherOf, motherOf, towardB);

        Integer lca = null;
        int best = Integer.MAX_VALUE;
        for (Map.Entry<Integer, Integer> e : depthA.entrySet()) {
            Integer db = depthB.get(e.getKey());
            if (db != null) {
                int sum = e.getValue() + db;
                if (sum < best) {
                    best = sum;
                    lca = e.getKey();
                }
            }
        }

        if (lca == null) {
            boolean spouses = spouseOf.containsKey(aId) && spouseOf.get(aId).equals(bId);
            String term = spouses ? "配偶" : "无血缘关系";
            String description = spouses
                    ? a.getName() + " 是 " + b.getName() + " 的 配偶"
                    : a.getName() + " 与 " + b.getName() + " 无血缘关系";
            List<String> path = List.of(a.getName(), b.getName());
            return new RelationshipVO(a.getName(), b.getName(), term, description, path);
        }

        int genA = depthA.get(lca);
        int genB = depthB.get(lca);
        String term = computeTerm(a, b, genA, genB, byId, towardA, towardB, lca);
        List<String> path = buildPath(a, b, lca, towardA, towardB, byId);

        return new RelationshipVO(a.getName(), b.getName(), term,
                a.getName() + " 是 " + b.getName() + " 的 " + term, path);
    }

    private Map<Integer, Integer> ancestorDepth(Integer root, Map<Integer, Integer> fatherOf,
                                                Map<Integer, Integer> motherOf, Map<Integer, Integer> toward) {
        Map<Integer, Integer> depth = new HashMap<>();
        Deque<Integer> queue = new ArrayDeque<>();
        queue.add(root);
        depth.put(root, 0);
        while (!queue.isEmpty()) {
            Integer cur = queue.poll();
            Integer d = depth.get(cur);
            Integer f = fatherOf.get(cur);
            Integer m = motherOf.get(cur);
            if (f != null && !depth.containsKey(f)) {
                depth.put(f, d + 1);
                toward.put(f, cur);
                queue.add(f);
            }
            if (m != null && !depth.containsKey(m)) {
                depth.put(m, d + 1);
                toward.put(m, cur);
                queue.add(m);
            }
        }
        return depth;
    }

    private String computeTerm(Person a, Person b, int genA, int genB, Map<Integer, Person> byId,
                               Map<Integer, Integer> towardA, Map<Integer, Integer> towardB, Integer lca) {
        if (genA == 0 && genB == 0) {
            return "本人";
        }
        if (genA == 0) {
            return ancestorTerm(a, b, genB, byId, towardB, lca);
        }
        if (genB == 0) {
            return descendantTerm(a, b, genA, byId, towardA, lca);
        }
        if (genA == 1 && genB == 1) {
            return siblingTerm(a, b);
        }
        if (genA == 1 && genB == 2) {
            return uncleAuntTerm(a, b, byId, towardB, lca);
        }
        if (genA == 2 && genB == 1) {
            return nephewNieceTerm(a, b, byId, towardA, lca);
        }
        if (genA == 2 && genB == 2) {
            return cousinTerm(a, b, byId, towardA, towardB, lca);
        }
        if (genA < genB) {
            return "第" + (genB - genA) + "代旁系长辈";
        }
        if (genA > genB) {
            return "第" + (genA - genB) + "代旁系晚辈";
        }
        return "第" + (genA - 1) + "代堂表亲";
    }

    private String ancestorTerm(Person a, Person b, int genB, Map<Integer, Person> byId,
                                Map<Integer, Integer> towardB, Integer lca) {
        boolean aMale = isMale(a);
        if (genB == 1) {
            return aMale ? "父亲" : "母亲";
        }
        if (genB == 2) {
            Person branch = byId.get(towardB.get(lca));
            boolean patrilineal = branch != null && isMale(branch);
            if (aMale) {
                return patrilineal ? "祖父" : "外祖父";
            }
            return patrilineal ? "祖母" : "外祖母";
        }
        if (genB == 3) {
            return aMale ? "曾祖父" : "曾祖母";
        }
        return aMale ? "第" + (genB - 1) + "代祖" : "第" + (genB - 1) + "代祖母";
    }

    private String descendantTerm(Person a, Person b, int genA, Map<Integer, Person> byId,
                                  Map<Integer, Integer> towardA, Integer lca) {
        boolean aMale = isMale(a);
        if (genA == 1) {
            return aMale ? "儿子" : "女儿";
        }
        if (genA == 2) {
            Person branch = byId.get(towardA.get(lca));
            boolean patrilineal = branch != null && isMale(branch);
            if (aMale) {
                return patrilineal ? "孙子" : "外孙";
            }
            return patrilineal ? "孙女" : "外孙女";
        }
        if (genA == 3) {
            return aMale ? "曾孙" : "曾孙女";
        }
        return aMale ? "第" + (genA - 1) + "代孙" : "第" + (genA - 1) + "代孙女";
    }

    private String siblingTerm(Person a, Person b) {
        boolean aMale = isMale(a);
        boolean bMale = isMale(b);
        boolean aOlder = olderThan(a, b);
        if (aMale && bMale) {
            return aOlder ? "兄" : "弟";
        }
        if (!aMale && !bMale) {
            return aOlder ? "姐" : "妹";
        }
        if (aMale) {
            return aOlder ? "兄" : "弟";
        }
        return aOlder ? "姐" : "妹";
    }

    private String uncleAuntTerm(Person a, Person b, Map<Integer, Person> byId,
                                 Map<Integer, Integer> towardB, Integer lca) {
        Person branch = byId.get(towardB.get(lca));
        boolean bParentMale = branch != null && isMale(branch);
        boolean aMale = isMale(a);
        if (bParentMale) {
            if (aMale) {
                return olderThan(a, branch) ? "伯父" : "叔父";
            }
            return "姑母";
        }
        return aMale ? "舅父" : "姨母";
    }

    private String nephewNieceTerm(Person a, Person b, Map<Integer, Person> byId,
                                   Map<Integer, Integer> towardA, Integer lca) {
        Person branch = byId.get(towardA.get(lca));
        boolean aParentMale = branch != null && isMale(branch);
        boolean aMale = isMale(a);
        if (aParentMale) {
            return aMale ? "侄子" : "侄女";
        }
        return aMale ? "外甥" : "外甥女";
    }

    private String cousinTerm(Person a, Person b, Map<Integer, Person> byId,
                              Map<Integer, Integer> towardA, Map<Integer, Integer> towardB, Integer lca) {
        Person branchA = byId.get(towardA.get(lca));
        Person branchB = byId.get(towardB.get(lca));
        boolean paternal = branchA != null && branchB != null && isMale(branchA) && isMale(branchB);
        String prefix = paternal ? "堂" : "表";
        boolean aMale = isMale(a);
        boolean bMale = isMale(b);
        boolean aOlder = olderThan(a, b);
        String suffix;
        if (aMale && bMale) {
            suffix = "兄弟";
        } else if (!aMale && !bMale) {
            suffix = "姐妹";
        } else if (aMale) {
            suffix = aOlder ? "兄" : "弟";
        } else {
            suffix = aOlder ? "姐" : "妹";
        }
        return prefix + suffix;
    }

    private List<String> buildPath(Person a, Person b, Integer lca, Map<Integer, Integer> towardA,
                                   Map<Integer, Integer> towardB, Map<Integer, Person> byId) {
        List<Integer> aChain = new ArrayList<>();
        Integer c = lca;
        while (c != null) {
            aChain.add(c);
            if (c.equals(a.getId())) {
                break;
            }
            c = towardA.get(c);
        }
        List<Integer> bChain = new ArrayList<>();
        c = lca;
        while (c != null) {
            bChain.add(c);
            if (c.equals(b.getId())) {
                break;
            }
            c = towardB.get(c);
        }
        List<String> path = new ArrayList<>();
        for (int i = aChain.size() - 1; i >= 0; i--) {
            path.add(byId.get(aChain.get(i)).getName());
        }
        for (int i = 1; i < bChain.size(); i++) {
            path.add(byId.get(bChain.get(i)).getName());
        }
        return path;
    }

    private boolean isMale(Person p) {
        return "男".equals(p.getGender());
    }

    private boolean olderThan(Person a, Person b) {
        if (a.getBirthDate() == null) {
            return false;
        }
        if (b.getBirthDate() == null) {
            return true;
        }
        return a.getBirthDate().isBefore(b.getBirthDate());
    }
}
