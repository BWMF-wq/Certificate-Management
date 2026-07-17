# 校园证书管理

一套面向高校学生的个人证书与荣誉管理 Web 应用。它把学习生涯中的语言证书、职业资格、竞赛获奖、技能认证、学术成果与荣誉表彰集中归档，并通过数据概况、证书管理和数据分析三个主要入口统一组织。

## 已实现功能

- 学号 / 邮箱注册登录，BCrypt 密码哈希与 JWT 无状态鉴权
- 学生之间的数据完全隔离，个人资料维护
- 证书新增、编辑、删除、分页、关键词搜索、成果类型 / 荣誉级别 / 奖项分类筛选和多种排序
- 荣誉级别统一为国家级荣誉、省市级荣誉、区县级荣誉、校级荣誉和其他荣誉
- 奖项分类统一为个人奖、团体奖和单位奖；有效期仅作为少数证书的可选档案字段，不提供到期提醒
- PDF、JPG、PNG、WebP 附件上传、替换、下载和删除（单文件最大 10MB）
- 证书总量、本年度新增、电子附件覆盖、颁发机构、荣誉级别、奖项构成和近 12 个月趋势统计
- 桌面端与移动端响应式界面
- Flyway 数据库版本管理、Docker Compose 部署、H2 集成测试

## 技术架构

| 层级 | 技术 |
| --- | --- |
| 前端 | Vue 3、TypeScript、Vite、Pinia、Vue Router、Axios、Lucide |
| 后端 | Java 17、Spring Boot 3、Spring Security、Spring Data JPA、JWT |
| 数据 | MySQL 8.4、Flyway；测试使用 H2 MySQL 兼容模式 |
| 部署 | Docker Compose、Nginx |

```text
浏览器 → 前端 Nginx → /api 反向代理 → Spring Boot → MySQL
                                      └→ 本地持久化附件卷
```

## 本地运行

### 方式一：Docker Compose（推荐）

1. 复制 `.env.example` 为 `.env`，修改数据库密码和 `JWT_SECRET`。
2. 在项目根目录运行：

```bash
docker compose up -d --build
```

3. 打开 `http://localhost`。后端健康检查为 `http://localhost:8080/api/health`。

默认会创建独立的 MySQL 和附件持久化卷。端口可通过 `MYSQL_PORT`、`BACKEND_PORT`、`FRONTEND_PORT` 环境变量调整。

### 方式二：分别启动

先准备 MySQL 数据库和用户：

```sql
CREATE DATABASE certificate_management CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
CREATE USER 'certificate_user'@'%' IDENTIFIED BY 'certificate_password';
GRANT ALL PRIVILEGES ON certificate_management.* TO 'certificate_user'@'%';
```

启动后端：

```bash
cd backend
mvn spring-boot:run
```

启动前端：

```bash
cd frontend
npm install
npm run dev
```

访问 `http://localhost:5173`。开发服务器会将 `/api` 代理到 `http://localhost:8080`。

## 配置项

| 变量 | 默认值 | 用途 |
| --- | --- | --- |
| `DB_URL` | 本机 `certificate_management` | JDBC 连接地址 |
| `DB_USERNAME` / `DB_PASSWORD` | `certificate_user` / `certificate_password` | 数据库凭据 |
| `JWT_SECRET` | 仅开发用默认值 | JWT 签名密钥，生产环境必须替换且不少于 32 字符 |
| `JWT_EXPIRATION` | `86400000` | 登录有效期（毫秒） |
| `STORAGE_PATH` | `./uploads` | 附件保存目录 |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:5173` | 允许的前端域名，多个值用逗号分隔 |
| `VITE_API_BASE_URL` | 跟随部署前缀的 `api` | 前端 API 根路径，通常无需设置 |
| `VITE_BASE_PATH` | `/` | 子路径部署前缀，例如 `/certpath/` |
| `FRONTEND_BIND` / `FRONTEND_PORT` | `0.0.0.0` / `80` | 前端监听地址与端口 |
| `BACKEND_BIND` / `BACKEND_PORT` | `0.0.0.0` / `8080` | 后端监听地址与端口 |

## 验证与构建

```bash
cd backend
mvn test
mvn package

cd ../frontend
npm run type-check
npm run build
```

集成测试覆盖注册、未授权拦截、证书创建、搜索与分类筛选、奖项分类、荣誉级别、数据概况统计和删除流程。

## 主要 API

- `POST /api/auth/register`、`POST /api/auth/login`
- `GET /api/users/me`、`PUT /api/users/me`
- `GET /api/certificates`、`POST /api/certificates`
- `GET|PUT|DELETE /api/certificates/{id}`
- `POST|GET|DELETE /api/certificates/{id}/attachment`
- `GET /api/dashboard`
- `GET /api/health`

除注册、登录和健康检查外，所有接口均要求 `Authorization: Bearer <token>`。

## 目录结构

```text
Certificate-Management/
├─ backend/                         Spring Boot 后端工程
│  ├─ Dockerfile                    后端镜像构建文件
│  ├─ pom.xml                       Maven 依赖与构建配置
│  └─ src/
│     ├─ main/java/com/campus/certificate/
│     │  ├─ config/                 安全、跨域与存储配置
│     │  ├─ controller/             登录、用户、证书、看板和健康接口
│     │  ├─ domain/                 用户、证书实体及枚举
│     │  ├─ dto/                    API 请求与响应对象
│     │  ├─ exception/              业务异常与统一错误响应
│     │  ├─ repository/             Spring Data JPA 数据访问层
│     │  ├─ security/               JWT 生成、校验与认证过滤器
│     │  └─ service/                认证、用户、证书与附件业务逻辑
│     ├─ main/resources/
│     │  ├─ application.yml         默认运行配置
│     │  ├─ application-e2e.yml     本地端到端测试配置
│     │  └─ db/migration/           Flyway 数据库迁移脚本
│     └─ test/                       Spring Boot 集成测试
├─ frontend/                        Vue 3 前端工程
│  ├─ Dockerfile                    前端构建与 Nginx 镜像
│  ├─ nginx.conf                    通用容器反向代理配置
│  ├─ nginx.cloud.conf              云服务器内部前端配置
│  ├─ package.json                  前端依赖与命令
│  └─ src/
│     ├─ api/                       Axios 客户端与接口封装
│     ├─ components/                证书卡片、表单和通用组件
│     ├─ composables/               Toast 等组合式逻辑
│     ├─ layouts/                   登录页与应用主布局
│     ├─ router/                    页面路由和登录守卫
│     ├─ stores/                    Pinia 登录状态
│     ├─ styles/                    全局主题与响应式样式
│     ├─ types/                     TypeScript 类型定义
│     └─ views/                     登录、注册、概览、证书和账户页面
├─ deploy/
│  ├─ setup-cloud-mysql.sh          Linux 服务器独立 MySQL 初始化
│  └─ setup-https.sh                Nginx、HTTPS 与证书续期配置
├─ e2e/
│  └─ smoke.py                      Playwright 线上全流程回归测试
├─ docker-compose.yml               本地 MySQL + 后端 + 前端编排
├─ docker-compose.cloud.yml         通用云服务器生产编排
├─ .env.example                     环境变量模板，不包含真实密钥
├─ .gitattributes                   跨平台行尾规则
├─ .gitignore                       依赖、构建产物与敏感文件忽略规则
└─ README.md
```

后端按照控制器、服务、数据访问和领域模型分层；前端按照页面、布局、组件、状态和接口分层。`docker-compose.yml` 用于本地完整环境，`docker-compose.cloud.yml` 用于当前 Linux 云服务器部署，并不绑定特定云厂商。生产密钥保存在服务器 `.env` 中，不进入 Git 仓库。

