# 仓库指南

## 项目结构与模块组织
- 根目录是 Maven 多模块聚合：`services`、`models`、`components`、`gateway`。
- `services` 当前包含 11 个子模块：`product`、`order`、`coupon`、`member`、`ware`、`file`、`search`、`auth`、`third-party`、`cart`、`ruoyi`。
- 业务模块主包以 `edu.nchu.mall.services.<module>` 为主；`file` 模块使用 `edu.nchu.shop.services.file`（跨模块联调时注意包名前缀差异）。
- Controller 实际存在两种分层：`...controller`（管理/CRUD）与 `...web`（页面/聚合接口），新增接口请沿用所在模块既有风格。
- 公共模型在 `models/src/main/java/edu/nchu/mall/models`（entity/dto/vo/enums 等）；公共配置与 Feign 在 `components/src/main/java/edu/nchu/mall/components`。
- 网关在 `gateway`；若依后台在 `services/ruoyi`（独立配置体系，含 `application.yml`、`application-druid.yml` 及代码生成模板）。
- 数据库 DDL 在 `sql/`（`product.sql`、`order.sql`、`coupon.sql`、`member.sql`、`ware.sql` 等），实体字段与注释变更需同步 DDL。

## 构建、测试与开发命令
- `mvn clean install`：全量构建与测试（提交前建议执行）。
- `mvn test`：运行全仓单测。
- `mvn -pl services/<module> -am package`：按模块构建（例如 `services/order`、`services/search`）。
- `mvn -pl services/<module> -am spring-boot:run`：本地启动指定服务并自动构建依赖。
- `mvn -pl gateway -am spring-boot:run`：启动网关。
- `mvn -pl services/ruoyi -am spring-boot:run`：启动若依模块（需先准备独立数据源配置）。

## 配置与依赖约定
- 大部分微服务通过 Nacos `shared-configs` 载入公共配置（常见：`commons-mysql`、`commons-redis`、`commons-redis-cache`、`commons`）。
- `order`、`product`、`ware` 使用 RabbitMQ 相关配置；`search` 依赖 Elasticsearch 配置；`auth/order` 还引用鉴权/支付相关配置。
- 默认本地连接信息以 `localhost` MySQL 为主（各模块库名不同，如 `product`、`order`、`coupon`、`member`、`ware`、`ry-vue`）。
- 修改缓存 key/TTL、MQ 路由键、Nacos 配置名时，需在 PR 写明兼容与发布步骤。

## 编码风格与命名规范
- Java：4 空格缩进、UTF-8、左大括号同行；类名 PascalCase、方法/字段 camelCase、包名全小写。
- MyBatis-Plus 实体保持 `@TableName/@TableId/@TableField` 完整；字段注解与数据库语义一致。
- 接口返回优先使用统一包装（`R`/`RCT`）；异常统一交由全局异常处理（`components` 中的 advice/exception 体系）。
- 新增 Feign 客户端优先放在 `components/.../feign/<domain>`，避免在各服务重复定义。

## 测试规范
- 测试目录：`src/test/java`，命名使用 `*Test` 或 `*Tests`，当前工程基于 JUnit 5（`org.junit.jupiter.api.Test`）。
- 优先编写稳定单测；外部依赖（Nacos/Redis/MQ/ES/DB）尽量 mock 或隔离，避免环境耦合。
- 若仅做手工验证或跳过测试，需在 PR 明确说明验证范围与风险。

## 提交与 PR 规范
- 提交信息：`type(scope): subject`（示例：`feat(order): add pay callback idempotency`）。
- PR 至少包含：变更概要、影响模块、测试命令与结果、回滚方案。
- 涉及 API 行为变更时，附 `curl` 示例或接口文档截图；涉及 DDL 时附迁移与兼容说明。

## 安全与配置提示
- 禁止提交密钥、密码、Token、短信/邮件凭据等敏感信息。
- 本地覆盖配置使用未跟踪文件（如 `application-local.yml` 或 IDE profile），不要直接改共享默认值并提交。
- 鉴权相关改动（JWT、网关过滤器、Spring Security）需补充最小权限与放行范围说明，避免误放开接口。
