# scloud

JDK 21 + Spring Boot 3.5.x + Spring Cloud 2025.0.x + Spring Cloud Alibaba 的标准微服务脚手架，参考 yudao-cloud / ruoyi-vue-pro 的模块划分与公共能力组织方式。

## 版本

- JDK: 21
- Spring Boot: 3.5.0
- Spring Cloud: 2025.0.2
- Spring Cloud Alibaba: 2025.0.0.0
- Nacos Server: 3.0.3
- MySQL: 8.x
- Redis / Redisson
- Nacos: 注册中心与配置中心
- MyBatis-Plus / Springdoc OpenAPI / Knife4j / Hutool / Lombok / MapStruct
- Gateway: Spring Cloud Gateway / LoadBalancer / Redis RateLimiter

版本选择遵循 [版本策略](docs/version-policy.md)：优先使用新稳定版本，但保持 JDK 21 + Spring Boot 3.5.x + Spring Cloud 2025.0.x + Spring Cloud Alibaba 适配版本这一兼容线。

## 模块

- `scloud-gateway`: 统一入口、JWT 鉴权、路由、CORS、Redis 限流
- `scloud-auth`: 注册、登录、刷新 token，区分 accessToken / refreshToken，禁用用户不可登录
- `scloud-system`: RBAC 管理接口，含用户、角色、菜单/按钮权限、部门、字典、岗位、用户角色、角色菜单权限、基础数据权限
- `scloud-demo`: 商品 CRUD、分页搜索、上下架、库存扣减、模拟购买，接口接入按钮权限校验
- `scloud-common`: 统一响应、异常、JWT、OpenAPI、MyBatis-Plus 配置
- `scloud-generator`: 读取 MySQL 元数据并生成 Entity、Mapper、Service、Controller、DTO、VO 源码
- 生成器输出每张表独立的 `XxxPageRequest` / `XxxSaveRequest`，分页查询字段来自表字段，并带 Knife4j 字段说明

## 本地启动

1. 启动基础设施：

```bash
docker compose up -d mysql redis nacos
```

2. 编译：

```bash
mvn clean package -DskipTests
```

3. 启动服务：

```bash
mvn -pl scloud-auth spring-boot:run
mvn -pl scloud-system spring-boot:run
mvn -pl scloud-demo spring-boot:run
mvn -pl scloud-generator spring-boot:run
mvn -pl scloud-gateway spring-boot:run
```

4. 或完整容器启动：

```bash
docker compose up -d --build
```

## 默认账号

- 用户名：`admin`
- 密码：`admin123`

登录接口：

```bash
curl -X POST http://localhost:9000/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"account":"admin","password":"admin123"}'
```

## 接口文档

- 网关 Knife4j 统一入口：`http://localhost:9000/doc.html`
- 在页面左上角分组切换 `认证中心`、`系统服务`、`演示服务`、`代码生成器`
- 统一入口依赖子服务已启动；本地开发时文档路由默认直连 `127.0.0.1:9201/9202/9203/9204`，Docker 环境通过 `SCLOUD_*_DOC_URI` 指向容器服务名
- 业务服务只输出 `/v3/api-docs`，Knife4j UI 统一由 Gateway 提供

## 系统管理接口

`scloud-system` 通过 Gateway  暴露 `/system/**`，默认需要 `Authorization: Bearer <accessToken>`。

- 用户：`/system/users`，支持增删改查、启用禁用、`/system/users/{id}/roles` 分配角色
- 角色：`/system/roles`，支持增删改查、启用禁用、`/system/roles/{id}/menus` 分配菜单/按钮权限
- 菜单权限：`/system/menus`，支持增删改查、启用禁用
- 部门：`/system/depts`，支持增删改查、启用禁用
- 字典：`/system/dicts`，支持增删改查、启用禁用
- 岗位：`/system/posts`，支持增删改查、启用禁用
- 列表接口均支持分页，但每个页面使用独立查询对象，并在 Knife4j 中展示字段说明。例如用户列表支持 `username`、`nickname`、`mobile`、`email`、`deptId`、`status`，角色列表支持 `roleName`、`roleKey`、`dataScope`、`status`
- 接口权限通过 `@RequirePermission` 标注，服务端根据 `sys_user_role`、`sys_role_menu`、`sys_menu.permission` 校验
- 用户列表支持基础数据权限：管理员/全部数据可见，本部门数据仅可见本部门，本人数据仅可见自己
- 代码生成器支持请求体 `templates` 覆盖或新增模板，支持 `{{className}}`、`{{packageName}}`、`{{tableName}}`、`{{fields}}` 占位符

## 配置

所有服务默认使用 `dev` profile，并支持通过环境变量覆盖：

- `NACOS_ADDR`
- `MYSQL_HOST`
- `MYSQL_PORT`
- `MYSQL_DATABASE`
- `MYSQL_USERNAME`
- `MYSQL_PASSWORD`
- `REDIS_HOST`
- `REDIS_PORT`
- `JWT_SECRET`

SQL 位于 `sql/mysql/schema.sql`，Docker Compose 首次启动 MySQL 时会自动初始化。

已有数据库需要补充执行 `sys_role_menu` 建表 SQL，或重新导入 `sql/mysql/schema.sql`。

Docker Compose 默认基础设施密码：

- MySQL root: `Aa123456`
- Redis: `Aa123456`
