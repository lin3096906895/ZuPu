# 族谱管理系统（ZuPu）

基于 **Spring Boot 3 + Vue 3** 的前后端分离族谱管理平台。用**图模型**建模真实族谱关系（多配偶/续弦、过继/承祧），内置**家族图谱可视化**、**AI 智能助手**（RAG + Function Calling + 流式输出）、**迁徙地图**（百度地图）和**统计看板**。

> 后端统一返回 `Result<T>` 结构，接口文档由 Swagger/OpenAPI 自动生成。

## 功能特性

- **认证与权限**：Spring Security + JWT 双 token（access/refresh），Redis 存 refresh token，BCrypt 密码（旧 SHA-256 惰性迁移）
- **家族 / 人物 / 婚姻 / 世代 / 事件 / 留言板**：完整 CRUD，支持多配偶/续弦、过继/承祧、世代深度管理
- **家族图谱**：祖先图 / 后代图 / 吊线图三种视图（AntV G6），环检测防 StackOverflow
- **关系查询**：任意两人之间的亲属关系路径计算
- **AI 助手**：Spring AI 对接 DeepSeek，RAG 注入族谱数据 + Function Calling 查库 + SSE 流式输出
- **迁徙地图**：百度地图 API，按世代展示家族成员出生地迁徙脉络
- **统计看板**：ECharts 世代人口分布、性别比例等统计
- **数据导入导出**：Excel（Apache POI）
- **缓存**：Redis + Spring Cache（家族树/统计）
- **接口文档**：Swagger / OpenAPI

## 页面一览

| 页面 | 路由 | 说明 |
|------|------|------|
| 仪表盘 | `/dashboard` | 家族/人口概览 |
| 家族管理 | `/families` | 家族的增删改查 |
| 人物管理 | `/persons` | 家族成员管理 |
| 人物详情 | `/persons/:id` | 单个人物档案 |
| 家族树 | `/tree` | 祖先/后代/吊线图 |
| 关系查询 | `/relationship` | 亲属关系路径 |
| 迁徙地图 | `/migration` | 百度地图迁徙脉络 |
| 统计看板 | `/stats` | 世代/性别统计 |
| AI 助手 | `/ai` | 对话式族谱问答 |

## 技术栈

| 层 | 技术 |
|----|------|
| 后端 | Spring Boot 3.2、Spring Security + JWT、MyBatis、MySQL 8、Redis、Flyway、Spring AI 1.0、Apache POI、PageHelper |
| 前端 | Vue 3、Vite、Pinia、Vue Router、Element Plus、AntV G6、ECharts、百度地图 JS API |
| 基础设施 | Docker Compose（MySQL + Redis + 后端 + 前端） |

## 项目结构

```
ZuPu
├── src/main/java/com/zupu/zupu/
│   ├── ai/          # AI 助手（工具函数 / Function Calling）
│   ├── aspect/      # AOP 操作日志切面
│   ├── common/      # 统一返回 Result、分页、异常
│   ├── config/      # Security、缓存、AI、跨域等配置
│   ├── controller/  # REST 接口（17 个控制器）
│   ├── dto/         # 请求/响应对象
│   ├── entity/      # 实体（家族/人物/婚姻/世代/事件/留言/用户）
│   ├── mapper/      # MyBatis Mapper 接口
│   ├── service/     # 业务层
│   └── util/        # 工具类（家族树构建、密码等）
├── src/main/resources/
│   ├── db/migration/   # Flyway 迁移脚本（自动建表 + 种子数据）
│   ├── mapper/         # MyBatis XML 映射
│   └── application.yml # 主配置（敏感项走环境变量）
├── frontend/           # Vue 3 前端
│   ├── Dockerfile / nginx.conf
│   └── src/
│       ├── api/        # axios 接口封装
│       ├── layout/     # 主布局
│       ├── router/     # 路由 + 登录守卫
│       ├── stores/     # Pinia 状态
│       ├── utils/      # 请求工具
│       └── views/      # 页面（9 个视图）
├── sql/                # 额外 SQL 脚本
├── docs/               # 文档（legacy/ 为早期设计的遗留脚本）
├── docker-compose.yml
├── Dockerfile
├── LICENSE
└── pom.xml
```

## 快速开始

### 方式一：Docker 一键部署（推荐）

```bash
# 1. 复制 .env.example 为 .env，填入 MySQL 密码与 DeepSeek API Key
cp .env.example .env

# 2. 复制 frontend/.env.example 为 frontend/.env，填入百度地图 AK
cp frontend/.env.example frontend/.env

# 3. 一键启动全部服务（MySQL + Redis + 后端 + 前端）
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
cd frontend && cp .env.example .env && npm install && npm run dev
```

访问 http://localhost:5173（Vite 代理将 `/api` 转发到后端 8080）。

## 环境变量

### 后端

| 变量 | 说明 | 默认值 |
|------|------|--------|
| `DB_HOST` / `DB_PORT` | MySQL 地址/端口 | `127.0.0.1` / `3307` |
| `DB_USERNAME` / `DB_PASSWORD` | 数据库账号/密码 | `root` / 必填 |
| `MYSQL_ROOT_PASSWORD` | MySQL 容器 root 密码（Docker 用） | 必填 |
| `REDIS_HOST` / `REDIS_PORT` | Redis 地址/端口 | `localhost` / `6379` |
| `DEEPSEEK_API_KEY` | DeepSeek API Key（AI 助手用） | 必填 |
| `JWT_SECRET` | JWT 签名密钥（生产务必更换，≥ 32 字节） | 内置占位值 |
| `CORS_ALLOWED_ORIGINS` | 允许跨域的前端来源，逗号分隔 | `http://localhost:5173` 等 |
| `DEMO_ACCOUNTS_ENABLED` | 是否启用内置演示账号，设为 `false` 会自动禁用未改密码的演示账号 | `true` |
| `AI_MAX_PROMPT_MEMBERS` | 注入 AI system prompt 的成员上限 | `200` |

### 前端

| 变量 | 说明 |
|------|------|
| `VITE_BAIDU_AK` | 百度地图浏览器端 AK，构建期注入 `index.html`（本地填 `frontend/.env`，Docker 填根目录 `.env` 的 `BAIDU_MAP_AK`） |

> 本地开发时也可在 `application-local.yml` 中直接填入后端密钥，该文件已被 `.gitignore` 忽略。

## 演示账号

- 管理员：`admin / admin123456`
- 普通用户：`demo / admin123456`

> ⚠️ **部署到公网前务必处理这两个账号。**
> 它们的密码是公开写在文档里的，任何人克隆仓库后都能直接登录。项目启动时会检测这一情况：
> 把 `DEMO_ACCOUNTS_ENABLED` 设为 `false`，仍在使用默认密码的演示账号会被自动禁用；
> 或者直接登录后修改密码，检测到密码已变更即不再干预。

## 部署到公网前的检查清单

- [ ] `JWT_SECRET` 换成随机值（≥ 32 字节），不要使用默认占位值
- [ ] `DEMO_ACCOUNTS_ENABLED=false`，或已修改 `admin` / `demo` 的密码
- [ ] `CORS_ALLOWED_ORIGINS` 改为你的真实域名，不要保留 `*`
- [ ] 数据库 root 密码、`DEEPSEEK_API_KEY` 通过环境变量注入，不要写进代码
- [ ] 百度地图 AK 在控制台配置 Referer 白名单，避免被盗刷配额
- [ ] 为 `/api/auth/register` 加上限流或邀请码，避免被批量注册

## 数据库说明

表结构由 Flyway 管理（`src/main/resources/db/migration/`），首次启动自动建表 + 种子数据。核心关系建模：

- `person`：双亲 `father_id` / `mother_id` + 过继 `adoptive_father_id` / `adoptive_mother_id` + 世代深度 `generation_no`
- `marriage`：独立婚姻表，支持多配偶/续弦

## 测试

```bash
./mvnw clean test
```

## 许可

本项目基于 [MIT License](LICENSE) 开源。
