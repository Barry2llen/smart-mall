# 智慧商城仓库架构概览

## 概览

该仓库是一个基于 Spring Boot 3.3、Spring Cloud 2023 与 Spring Cloud Alibaba 2023 的 Maven 多模块后端项目。整体可分为 4 层：

- 聚合与共享层：`pom.xml`、`models`、`components`
- 接入层：`gateway`、`services/auth`、`services/ruoyi`
- 业务服务层：`product`、`coupon`、`ware`、`member`、`cart`、`order`、`search`、`flash-sale`、`third-party`
- 数据与基础设施层：MySQL、Redis/Redisson、RabbitMQ、Elasticsearch、Nacos、邮件、OSS、支付

其中：

- `models` 是编译期共享模型层，承载 entity、dto、vo、mq 消息体、校验与回调模型
- `components` 是编译期共享组件层，承载 Feign Client、全局异常、AOP、线程池、工具类与回调支持
- `gateway` 是统一业务入口，负责路由、CORS、JWT 校验与用户身份透传
- `ruoyi` 是仓内独立后台子系统，复用 Redis/Nacos，但不参与商城主链路的 Feign/MQ 编排

## 仓库结构图

```mermaid
graph TD
    ROOT["smart-mall<br/>Maven 聚合根"]

    ROOT --> POM["pom.xml<br/>父 POM / 依赖管理"]
    ROOT --> GATEWAY["gateway<br/>统一入口 / 路由 / JWT"]
    ROOT --> SERVICES["services<br/>业务与后台运行模块"]
    ROOT --> COMPONENTS["components<br/>共享组件层"]
    ROOT --> MODELS["models<br/>共享模型层"]
    ROOT --> SQL["sql<br/>各业务库 DDL"]
    ROOT --> DOCS["docs<br/>项目文档"]

    SERVICES --> AUTH["auth"]
    SERVICES --> CART["cart"]
    SERVICES --> COUPON["coupon"]
    SERVICES --> FLASH["flash-sale"]
    SERVICES --> MEMBER["member"]
    SERVICES --> ORDER["order"]
    SERVICES --> PRODUCT["product"]
    SERVICES --> RUOYI["ruoyi"]
    SERVICES --> SEARCH["search"]
    SERVICES --> THIRD["third-party"]
    SERVICES --> WARE["ware"]

    MODELS --> MODELS_SUB["entity / dto / vo / enums / mq TO / validation / callback"]
    COMPONENTS --> COMP_SUB["feign / advice / aspect / config / utils / exception / notice"]

    COMPONENTS --> AUTH
    COMPONENTS --> CART
    COMPONENTS --> COUPON
    COMPONENTS --> FLASH
    COMPONENTS --> MEMBER
    COMPONENTS --> ORDER
    COMPONENTS --> PRODUCT
    COMPONENTS --> SEARCH
    COMPONENTS --> THIRD
    COMPONENTS --> WARE

    MODELS --> AUTH
    MODELS --> CART
    MODELS --> COUPON
    MODELS --> FLASH
    MODELS --> MEMBER
    MODELS --> ORDER
    MODELS --> PRODUCT
    MODELS --> SEARCH
    MODELS --> THIRD
    MODELS --> WARE
```

说明：

- `services` 下当前有效运行单元共 11 个
- `components` 与 `models` 不是独立部署服务，而是业务模块的共享依赖
- `sql` 对应商品、订单、优惠、会员、仓储等业务库结构

## 运行时拓扑图

```mermaid
graph LR
    subgraph Clients["外部调用方"]
        WEB["商城前台 / H5 / Web"]
        ADMIN_UI["后台管理前端"]
    end

    subgraph Entry["接入层"]
        GW["Gateway<br/>8080<br/>路由 / JWT / CORS / Header透传"]
        AUTH["Auth<br/>9000<br/>登录 / 注册 / Token 刷新"]
        RUOYI["RuoYi<br/>8081<br/>后台管理 / 权限 / 监控 / 代码生成"]
    end

    subgraph Biz["商城业务服务"]
        PRODUCT["Product<br/>8000<br/>商品 / SPU / SKU / 分类 / 品牌"]
        COUPON["Coupon<br/>8200<br/>优惠券 / 满减 / 秒杀场次"]
        MEMBER["Member<br/>8300<br/>会员 / 地址 / 积分 / 收藏"]
        WARE["Ware<br/>8400<br/>仓库 / 采购 / 库存 / 锁库存"]
        SEARCH["Search<br/>8800<br/>商品检索 / ES 聚合"]
        FLASH["FlashSale<br/>8989<br/>秒杀缓存 / 限流 / 异步下单"]
        AUTHSVC["Auth Service APIs<br/>9000"]
        THIRD["Third-Party<br/>9100<br/>邮件 / OSS / 回调"]
        CART["Cart<br/>9400<br/>购物车 / 勾选 / 数量刷新"]
        ORDER["Order<br/>8100<br/>确认单 / 下单 / 支付 / 关单"]
    end

    subgraph Infra["基础设施"]
        NACOS["Nacos<br/>注册中心 + 配置中心"]
        MYSQL["MySQL<br/>product / order / coupon / member / ware / ry-vue"]
        REDIS["Redis / Redisson<br/>缓存 / Token / 分布式锁 / 信号量"]
        RABBIT["RabbitMQ<br/>订单 / 库存 / 商品上架 / 秒杀事件"]
        ES["Elasticsearch<br/>商品检索索引"]
        OSS["阿里云 OSS / STS"]
        MAIL["邮件服务"]
        PAY["支付宝回调"]
    end

    WEB --> GW
    ADMIN_UI --> RUOYI

    GW --> PRODUCT
    GW --> MEMBER
    GW --> WARE
    GW --> SEARCH
    GW --> AUTH
    GW --> CART
    GW --> ORDER
    GW --> COUPON
    GW --> FLASH
    GW --> THIRD
    GW --> RUOYI

    PRODUCT --> NACOS
    COUPON --> NACOS
    MEMBER --> NACOS
    WARE --> NACOS
    SEARCH --> NACOS
    FLASH --> NACOS
    AUTH --> NACOS
    CART --> NACOS
    ORDER --> NACOS
    THIRD --> NACOS
    RUOYI --> NACOS
    GW --> NACOS

    PRODUCT --> MYSQL
    COUPON --> MYSQL
    MEMBER --> MYSQL
    WARE --> MYSQL
    ORDER --> MYSQL
    RUOYI --> MYSQL

    PRODUCT --> REDIS
    COUPON --> REDIS
    MEMBER --> REDIS
    WARE --> REDIS
    FLASH --> REDIS
    AUTH --> REDIS
    CART --> REDIS
    ORDER --> REDIS
    THIRD --> REDIS
    RUOYI --> REDIS

    PRODUCT --> RABBIT
    MEMBER --> RABBIT
    WARE --> RABBIT
    SEARCH --> RABBIT
    FLASH --> RABBIT
    ORDER --> RABBIT

    SEARCH --> ES
    THIRD --> OSS
    THIRD --> MAIL
    ORDER --> PAY

    AUTHSVC -. 同实例对外接口 .- AUTH
```

说明：

- `Gateway(8080)` 是统一商城入口，负责按路径前缀转发到各微服务
- `RuoYi(8081)` 是独立后台子系统，直接对接后台前端，不经过商城业务 Feign/MQ 编排
- `Search` 不依赖 MySQL，本地配置引入 `commons-elastic`
- `FlashSale` 强依赖 Redis/Redisson 与 RabbitMQ，用于秒杀活动缓存、用户资格控制、库存信号量和异步订单联动

## 服务通信图

```mermaid
graph LR
    AUTH["auth"]
    CART["cart"]
    PRODUCT["product"]
    COUPON["coupon"]
    MEMBER["member"]
    WARE["ware"]
    ORDER["order"]
    SEARCH["search"]
    FLASH["flash-sale"]
    THIRD["third-party"]
    RABBIT["RabbitMQ"]

    AUTH -->|Feign: 会员注册/登录资料| MEMBER
    AUTH -->|Feign: 邮件验证码| THIRD

    CART -->|Feign: SKU/价格/属性| PRODUCT
    CART -->|Feign: 库存状态| WARE

    PRODUCT -->|Feign: 优惠/积分信息| COUPON
    PRODUCT -->|Feign: 仓储/库存信息| WARE

    ORDER -->|Feign: 勾选购物项| CART
    ORDER -->|Feign: 会员/收货地址| MEMBER
    ORDER -->|Feign: SPU/SKU 信息| PRODUCT
    ORDER -->|Feign: 锁库存| WARE

    WARE -->|Feign: 订单状态查询| ORDER

    FLASH -->|Feign: 秒杀场次| COUPON
    FLASH -->|Feign: SKU 信息| PRODUCT
    FLASH -->|Feign: 用户地址| MEMBER

    PRODUCT -.->|MQ: 商品上架写入检索索引| RABBIT
    RABBIT -.->|product.spu.elastic.*| SEARCH

    ORDER -.->|MQ: order.create / order.release| RABBIT
    RABBIT -.->|order.stock.release / stock.release.*| WARE

    WARE -.->|MQ: stock.delay / stock.release| RABBIT
    RABBIT -.->|stock.release.queue| WARE

    FLASH -.->|MQ: order.flashsale.create| RABBIT
    RABBIT -.->|order.flashsale.queue| ORDER

    MEMBER -.->|MQ: flashsale.event.userinfochanged| RABBIT
    ORDER -.->|MQ: flashsale.cancel| RABBIT
    RABBIT -.->|flashsale.event.queue / cancel.queue| FLASH
```

说明：

- 实线表示同步调用，核心手段是 `components` 中统一定义的 OpenFeign Client
- 虚线表示异步事件，核心用于商品上架检索、订单延迟关单、库存释放、秒杀订单联动
- `search` 主要通过消费商品上架消息维护 ES 索引，而不是直接被其他业务服务调用写索引

## 关键时序图

```mermaid
sequenceDiagram
    autonumber
    actor User as 前台用户
    participant GW as Gateway
    participant Auth as Auth
    participant Member as Member
    participant Third as Third-Party
    participant Redis as Redis
    participant Flash as FlashSale
    participant Order as Order
    participant Ware as Ware
    participant MQ as RabbitMQ

    rect rgb(245,245,245)
        Note over User,Redis: 场景一：登录鉴权链路
        User->>GW: POST /auth/public/sendCode
        GW->>Auth: 转发白名单请求
        Auth->>Third: Feign 发送验证码邮件
        Third->>Redis: 写入验证码/频控信息
        Third-->>Auth: 发送结果
        Auth-->>User: 发送成功/失败

        User->>GW: POST /auth/public/login
        GW->>Auth: 转发白名单请求
        Auth->>Member: Feign 校验用户信息
        Auth->>Redis: 刷新会话/Token 辅助状态
        Auth-->>User: Header 返回 access token + Cookie 返回 refresh token

        User->>GW: 请求任意 /public 受保护业务接口
        GW->>GW: JwtAuthGlobalFilter 校验 JWT
        GW->>GW: 注入 X-User-Id 请求头
        GW-->>User: 转发到下游业务服务
    end

    rect rgb(235,245,255)
        Note over User,MQ: 场景二：下单、库存释放与秒杀联动
        User->>GW: 提交普通订单或秒杀订单
        alt 普通订单
            GW->>Order: /order/public/submit
            Order->>Member: 查询地址/会员信息
            Order->>Cart: 查询已勾选购物项
            Order->>Product: 查询 SPU/SKU 信息
            Order->>Ware: 锁定库存
            Order->>MQ: 发送 order.create
        else 秒杀订单
            User->>GW: /flash-sale/public/flash-sale/kill
            GW->>Flash: 发起秒杀
            Flash->>Redis: 校验场次、随机码、限购、用户资格、信号量
            Flash->>MQ: 发送 order.flashsale.create
            MQ->>Order: 消费秒杀下单消息
            Order->>Member: 查询地址/会员信息
            Order->>Product: 查询商品信息
            Order->>Ware: 锁定库存
            Order->>MQ: 发送 order.create
        end

        MQ->>Order: 延迟到期投递到 order.release.queue
        Order->>Order: 关闭未支付订单
        Order->>MQ: 发送 order.stock.release
        MQ->>Ware: 通知解锁库存
        Ware->>Order: Feign 查询订单状态
        Ware->>Ware: 解锁 LOCKED 库存明细

        opt 订单来源于秒杀
            Order->>MQ: 发送 flashsale.cancel
            MQ->>Flash: 回收秒杀占用状态
            Flash->>Redis: 删除购买记录并回收资格
        end
    end
```

说明：

- 登录链路的关键控制点在网关：白名单放行、JWT 校验、用户身份透传
- 普通订单与秒杀订单最终都归并到 `order` 和 `ware` 的统一订单/库存主链路
- `flash-sale` 自身不直接落订单库，而是通过 MQ 将秒杀订单创建事件委托给 `order`

## 服务清单表

| 服务 | 端口 | 主要职责 | 入口类型 | 主要中间件/配置 | 主要上游 | 主要下游 |
| --- | --- | --- | --- | --- | --- | --- |
| `gateway` | `8080` | 统一入口、路由转发、JWT 校验、Header 透传、CORS | Gateway Route + GlobalFilter | Nacos `sentinel`、Spring Security、JWT | 商城前台 | `product/member/ware/search/auth/cart/order/coupon/flash-sale/third-party/ruoyi` |
| `auth` | `9000` | 登录、注册、验证码、Token 刷新/登出 | `web` | Nacos `commons-mysql/commons-redis/commons-redis-cache/commons/oauth2/jwt/sentinel`、Spring Security、Spring Session Redis | Gateway | `member`、`third-party`、Redis |
| `product` | `8000` | 商品、分类、品牌、SPU/SKU、商品详情 | `controller` + `web` | Nacos `commons-mysql/commons-redis/commons-redis-cache/commons-callback/commons/rabbit/sentinel`、Redis、RabbitMQ | Gateway、`cart`、`order`、`flash-sale` | `coupon`、`ware`、RabbitMQ(`search`) |
| `coupon` | `8200` | 优惠券、满减、会员价、秒杀场次/关系 | `controller` | Nacos `commons-mysql/commons-redis/commons-redis-cache/commons/sentinel`、Redis | `product`、`flash-sale`、Gateway | MySQL、Redis |
| `member` | `8300` | 会员、地址、积分、成长值、收藏 | `controller` | Nacos `commons-mysql/commons-redis/commons-redis-cache/commons/sentinel`、Redis、RabbitMQ | `auth`、`order`、`flash-sale`、Gateway | RabbitMQ(`flash-sale`) |
| `ware` | `8400` | 仓库、采购、库存查询、锁库存、解锁库存 | `controller` | Nacos `commons-mysql/commons-redis/commons-redis-cache/commons/rabbit/sentinel`、Redis、RabbitMQ | `product`、`cart`、`order`、Gateway | `order`、RabbitMQ |
| `search` | `8800` | 商品搜索、条件过滤、ES 聚合查询 | `controller` + `web` | Nacos `commons/commons-elastic/rabbit/sentinel`、Elasticsearch、RabbitMQ | Gateway、RabbitMQ(`product`) | Elasticsearch |
| `flash-sale` | `8989` | 秒杀场次缓存、资格控制、随机码、信号量、异步秒杀下单 | `web` | Nacos `commons-redis/commons/rabbit/sentinel`、Redis、Redisson、RabbitMQ | Gateway、RabbitMQ(`member/order`) | `coupon`、`product`、`member`、RabbitMQ(`order`) |
| `third-party` | `9100` | 邮件验证码、OSS 上传签名、文件回调 | `controller` | Nacos `commons-redis/commons/commons-mail/commons-file/commons-callback/sentinel`、Redis、邮件、OSS | `auth`、Gateway、`product` | 外部邮件、阿里云 OSS |
| `cart` | `9400` | 购物车查询、增删改、勾选、刷新库存价格 | `controller` + `web` | Nacos `commons-mysql/commons-redis/commons-redis-cache/commons/cart/sentinel`、Redis | Gateway、`order` | `product`、`ware` |
| `order` | `8100` | 确认单、提交订单、支付回调、订单关闭、库存释放协同 | `controller` + `web` | Nacos `commons-mysql/commons-redis/commons-redis-cache/commons-feign/commons/rabbit/alipay/sentinel`、Redis、RabbitMQ、支付回调 | Gateway、RabbitMQ(`flash-sale`) | `cart`、`member`、`product`、`ware`、RabbitMQ |
| `ruoyi` | `8081` | 后台用户、角色、菜单、监控、定时任务、代码生成 | RuoYi Controllers | Nacos `commons-redis`、Spring Security、Redis、独立 `application.yml`/`application-druid.yml` | 后台前端 | MySQL、Redis |

## 说明与已知命名差异

### 1. `file` 与 `third-party` 的历史命名并存

- 共享 Feign 客户端 `components.feign.file.OSSFeignClient` 仍然使用 `@FeignClient("file")`
- 但当前仓库实际提供 OSS/邮件能力的运行服务是 `services/third-party`
- 网关也声明了 `id: file`，并将 `/file/**` 流量路由到 `lb://third-party`

这说明仓库中仍残留“文件服务独立部署”时期的命名，运行时语义已经收敛到 `third-party`

### 2. 网关 `/file/**` 路由与 RewritePath 写法不完全对称

- 当前网关谓词是 `/file/**`
- 但对应过滤器写的是 `RewritePath=/third-party/?(?<segment>.*), /${segment}`

文档中的运行时拓扑按“配置声明意图”建模为 `/file/** -> third-party`。若后续联调出现文件上传路径问题，应优先复核这条 RewritePath 配置

### 3. `ruoyi` 不属于商城主业务编排链

- `ruoyi` 虽然与商城服务处于同一聚合仓库
- 但它采用自身的控制器、配置和安全体系
- 它不参与当前商城业务的 Feign 互调主链，也不消费订单、库存、秒杀相关消息

### 4. `models` 与 `components` 是“共享层”而不是“平台服务”

- 架构图中单独绘制这两层，是为了表达代码复用关系
- 它们不会在运行时以独立进程启动
