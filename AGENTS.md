# 仓库指南

## 项目结构与模块组织
- 根目录是 Maven 多模块：`models`（共享实体/DTO）、`components`（通用配置、缓存、全局异常等公共组件）、`services`（业务服务集合）、`gateway`（网关）、`ruoyi`（若依相关模块）。
- `services` 下包含：`product`、`order`、`coupon`、`member`、`ware`，各模块均有 `src/main/java` 与 `src/main/resources`。
- Controller 约定位于 `edu.nchu.mall.services.<module>.controller`；Swagger/Knife4j 扫描包需与模块匹配（例如 order：`edu.nchu.mall.services.order.controller`）。
- 数据库 DDL 放在 `sql/`（如 `sql/order.sql`），实体字段需与表结构/注释保持同步。

## 构建、测试与开发命令
- `mvn clean install`：全量构建 + 测试，提交前建议执行。
- `mvn test`：运行所有单测。
- `mvn -pl services/order -am package`：按模块快速构建（将 `order` 替换为其他模块名）。
- `mvn -pl services/order -am spring-boot:run`：本地启动指定服务并构建依赖。

## 编码风格与命名规范
- Java：4 空格缩进、UTF-8、左大括号同行；类名 PascalCase、方法/字段 camelCase、包名小写点分。
- MyBatis-Plus 实体需使用 `@TableName/@TableField/@TableId`；每个字段添加 `@Schema` 且语义与数据库一致。
- Controller 返回统一的 `R`/`RCT` 包装；REST 路径按领域前缀（`/orders`、`/order-items`、`/payment-infos` 等）。
- SQL/迁移文件命名需语义化（如 `order-ops-202601.sql`）。

## 测试规范
- 测试放在 `src/test/java`，与目标类同名并加 `*Test` 后缀（如 `OrderServiceImplTest`）。
- 优先使用 JUnit 5；外部依赖（DB/Redis/Nacos）尽量 mock，保证测试稳定快速。
- 如需跳过测试或手工验证，请在 PR 中说明；默认期望 `mvn test` 通过。

## 提交与 PR 规范
- 提交格式：`type(scope): subject`（如 `feat(order): add refund api`），subject ≤ 50 字符，正文 72 字符换行。
- PR 需包含变更概要、测试命令/结果、关联 Issue/Jira；API/行为改动需附截图或 curl 示例。
- 涉及 Nacos/Redis/DB 变更时，需说明兼容性与发布/回滚要点。

## 安全与配置提示
- 禁止提交敏感信息；本地覆盖配置请放入忽略文件（如 `application-local.yml`）。
- 调整缓存 key/TTL 或 DDL 时，请在 PR 说明兼容与部署步骤。
