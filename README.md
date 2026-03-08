

# Smart-Mall 智慧商城分布式系统

## 📖 项目简介

Smart-Mall 这是一个基于 Spring Boot 3.3、Spring Cloud 2023 与 Spring Cloud Alibaba 2023 构建的大型分布式电商业务平台。项目采用了成熟的微服务架构设计，涵盖了从前台购物（商品、购物车、订单、秒杀）到后台管理（接入若依系统）的完整电商链路。

本项目重点解决了高并发场景下的秒杀限流、分布式缓存锁、跨服务异步通信以及分布式库存一致性等核心技术痛点，致力于提供一个高性能、高可用的后端解决方案。

## 🛠️ 核心技术栈

* **微服务基础**: Spring Boot 3.3, Spring Cloud 2023, Spring Cloud Alibaba 2023
* **注册与配置中心**: Nacos
* **数据库**: MySQL
* **缓存与分布式锁**: Redis, Redisson (底层缓存链接已全面替换为 Redisson 以提供更强大的分布式锁与并发控制能力)
* **消息中间件**: RabbitMQ
* **搜索引擎**: Elasticsearch
* **安全与认证**: Spring Security, JWT (无状态鉴权)
* **第三方服务**: 阿里云 OSS, 邮件服务, 支付宝沙箱支付

## 🏗️ 系统架构

整个系统架构经过精心分层，保证了职责清晰与代码复用，主要划分为 4 个核心层：

1. **接入层 (Entry)**: Gateway 统一网关负责路由、CORS、全局 JWT 校验与身份透传；Auth 提供鉴权服务；独立集成 RuoYi 作为后台管理入口。
2. **业务服务层 (Biz)**: 拆分为商品(Product)、优惠(Coupon)、仓储(Ware)、会员(Member)、购物车(Cart)、订单(Order)、检索(Search)、秒杀(Flash-Sale)及第三方服务(Third-Party)等 9 大核心微服务。
3. **聚合与共享层 (Shared)**: `models` 提供全过程共享的 Entity、DTO、VO 等模型；`components` 提供统一的 Feign AOP、异常处理与线程池等基础组件。
4. **数据与基础设施层 (Infra)**: MySQL 业务分库、Redis 集群、RabbitMQ 消息总线与 ES 搜索引擎。

## ✨ 核心技术亮点

* **高并发秒杀方案 (`flash-sale`)**:
* 不直接操作订单库，而是通过 Redis/Redisson 进行秒杀场次预热、用户资格校验与信号量控制。
* 请求通过校验后，发送消息至 RabbitMQ `order.flashsale.create` 队列，由订单系统异步削峰消费，极大提升了系统吞吐量并保护了底层数据库。


* **最终一致性与可靠性保证 (`order` & `ware`)**:
* 深度应用 RabbitMQ 延迟队列。订单创建后发送延迟消息，若超时未支付，自动触发关单逻辑并发送 `order.stock.release` 消息。
* 仓储服务订阅库存解锁消息，通过状态机校验确保库存的安全释放，完美解决分布式场景下的网络波动与异常导致的库存死锁问题。


* **海量数据聚合检索 (`search`)**:
* 基于 Elasticsearch 搭建商品检索系统，通过监听 RabbitMQ 的商品上架消息 (`product.spu.elastic.*`) 实现 ES 索引数据的异步更新，做到业务解耦。


* **统一网关与鉴权 (`gateway` & `auth`)**:
* 通过自定义 `JwtAuthGlobalFilter` 实现全局登录拦截与令牌校验。
* 网关层校验通过后，将用户身份信息抽取并注入请求头 (`X-User-Id`) 透传给下游业务微服务，实现了各个业务服务的无状态化与安全调用。



## 📂 项目模块结构

```text
smart-mall
├── gateway         # 统一 API 网关 (端口: 8080)
├── services        # 微服务业务模块
│   ├── auth        # 认证与授权中心 (端口: 9000)
│   ├── product     # 商品系统 (端口: 8000)
│   ├── order       # 订单系统 (端口: 8100)
│   ├── ware        # 仓储与库存系统 (端口: 8400)
│   ├── member      # 会员系统 (端口: 8300)
│   ├── cart        # 购物车系统 (端口: 9400)
│   ├── coupon      # 营销与优惠券系统 (端口: 8200)
│   ├── search      # Elasticsearch 检索系统 (端口: 8800)
│   ├── flash-sale  # 高并发秒杀模块 (端口: 8989)
│   ├── third-party # OSS/邮件/支付等第三方集成 (端口: 9100)
│   └── ruoyi       # 独立的后台管理系统 (端口: 8081)
├── components      # 公共组件包 (Feign Client, 全局异常, 拦截器等)
├── models          # 公共模型包 (Entity, DTO, VO, Enums, MQ 载体等)
└── sql             # 各微服务的初始化 DDL/DML 脚本

```