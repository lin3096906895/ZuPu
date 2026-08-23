# 族谱管理系统（ZuPu）

基于 Spring Boot 3 + Vue 3 的前后端分离族谱管理平台。用**图模型**建模真实族谱关系（多配偶/续弦、过继/承祧），内置**家族图谱可视化**、**AI 智能助手**（RAG + Function Calling + 流式）和**统计看板**。

## 功能特性

- **认证与权限**：Spring Security + JWT 双 token，Redis 存 refresh token，BCrypt 密码（旧 SHA-256 惰性迁移）
- **家族/人物/婚姻/世代/事件/留言板**：完整 CRUD
- **家族图谱**：祖先图 / 后代图 / 吊线图三种视图（AntV G6），环检测防 StackOverflow
- **AI 助手**：Spring AI 对接 DeepSeek，RAG 注入族谱数据 + Function Calling 查库 + SSE 流式输出
- **统计看板**：ECharts 世代人口分布、性别比例
- **数据导入导出**：Excel（Apache POI）
- **缓存**：Redis + Spring Cache（家族树/统计）
- **接口文档**：Swagger / OpenAPI

## 技术栈

| 层 | 技术 |
|----|------|
| 后端 | Spring Boot 3.2、Spring Security + JWT、MyBatis、MySQL 8、Redis、Flyway、Spring AI 1.0 |
| 前端 | Vue 3、Vite、Pinia、Vue Router、Element Plus、AntV G6、ECharts |
| 基础设施 | Docker Compose |

## 快速开始

### 方式一：Docker 一键部署（推荐）

```bash
# 1. 复制 .env.example 为 .env，填入 MySQL 密码与 DeepSeek API Key
# 2. 一键启动全部服务（MySQL + Redis + 后端 + 前端）
docker compose up -d --build
```

- 前端：http://localhost:5173
- 后端接口文档：http://localhost:8080/swagger-ui/index.html

### 方式二：本地开发

```bash
# 1. 启动基础设施
docker compose up -d mysql redis

# 2. 复制 application-local.yml.example 为 application-local.yml 填入密钥，启动后端
./mvnw spring-boot:run -Dspring-boot.run.profiles=local

# 3. 启动前端
cd frontend && npm install && npm run dev
```

访问 http://localhost:5173（Vite 代理转发 `/api` 到后端）。

## 演示账号

- 管理员：`admin / admin123456`
- 普通用户：`demo / admin123456`

## 数据库说明

表结构由 Flyway 管理（`src/main/resources/db/migration/`），首次启动自动建表 + 种子数据。核心关系建模：

- `person`：双亲 `father_id`/`mother_id` + 过继 `adoptive_father_id`/`adoptive_mother_id` + 世代深度 `generation_no`
- `marriage`：独立婚姻表，支持多配偶/续弦

## 测试

```bash
./mvnw clean test
```
