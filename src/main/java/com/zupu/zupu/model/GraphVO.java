package com.zupu.zupu.model;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 家族图谱数据：节点 + 边，供前端 G6 渲染。
 */
@Data
public class GraphVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Node> nodes = new ArrayList<>();
    private List<Edge> edges = new ArrayList<>();

    @Data
    public static class Node implements Serializable {
        private static final long serialVersionUID = 1L;
        private Integer id;
        private String name;
        private String gender;
        private Integer generationNo;
        private Integer fatherId;
        private Integer motherId;
        private LocalDate birthDate;
        private LocalDate deathDate;
        private List<String> spouseNames;
    }

    @Data
    public static class Edge implements Serializable {
        private static final long serialVersionUID = 1L;
        private Integer source;
        private Integer target;
        private String type; // FATHER / MOTHER / MARRIAGE

        public Edge(Integer source, Integer target, String type) {
            this.source = source;
            this.target = target;
            this.type = type;
        }
    }
}
