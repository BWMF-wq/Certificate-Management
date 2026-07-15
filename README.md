# 证途 · 校园证书管理

证途（CertPath）是一套面向高校学生的个人证书管理 Web 应用。它把学习生涯中的语言证书、职业资格、竞赛获奖、技能认证、学术成果与荣誉表彰集中归档，并在证书到期前 90 天主动标记提醒。

## 已实现功能

- 学号 / 邮箱注册登录，BCrypt 密码哈希与 JWT 无状态鉴权
- 学生之间的数据完全隔离，个人资料维护
- 证书新增、编辑、删除、分页、关键词搜索、分类 / 状态筛选和多种排序
- 自动识别长期有效、有效、90 天内到期、已到期四种状态
- PDF、JPG、PNG、WebP 附件上传、替换、下载和删除（单文件最大 10MB）
- 证书总量、状态分布、分类构成、近 12 个月成长趋势和最近获得统计
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

集成测试覆盖注册、未授权拦截、证书创建、搜索与分类筛选、到期状态、仪表盘统计和删除流程。

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
├─ backend/                 Spring Boot API、迁移与测试
├─ frontend/                Vue 3 应用与 Nginx 容器配置
├─ docker-compose.yml       MySQL + 后端 + 前端编排
├─ .env.example             环境变量模板
└─ README.md
```

## 生产注意事项

- 必须替换示例密码和 JWT 密钥，不要提交 `.env`。
- 建议由云端 Nginx / 网关统一终止 HTTPS，并只开放前端端口；MySQL 当前仅绑定服务器回环地址。
- `uploads` 与 MySQL 数据卷需要纳入定期备份策略。
- 附件接口已做用户归属、扩展类型、MIME 类型、大小与路径穿越检查；若面向大规模用户，建议替换为 OSS 对象存储。

### 当前阿里云部署

服务器部署使用 `docker-compose.cloud.yml`：前端监听 `127.0.0.1:6060`，后端使用 host 网络监听 `127.0.0.1:18081`，证途专用 MySQL 实例监听 `127.0.0.1:13306`。公网由 Nginx 在 443 端口终止 TLS，并将 `/api/` 直接转发到后端；80 端口强制跳转。应用地址为 `https://test.falshcode.xyz`，可执行 `deploy/setup-https.sh` 完成免费证书签发、安全响应头与自动续期配置。
