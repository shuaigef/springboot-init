# SpringBoot 项目初始模板

> 作者：shuaigef

基于 Java SpringBoot 的项目初始模板，整合了常用框架和主流业务的示例代码。

## 模板特点

### 主流框架 & 特性

- Spring Boot 2.7.x
- Spring MVC
- MyBatis + MyBatis Plus 数据访问（开启分页）
- Spring AOP 切面编程
- Spring 事务注解
- SaToken 轻量级权限认证

### 数据存储

- MySQL 数据库
- Redis 内存数据库

### 工具类

- Hutool 工具库
- Gson 解析库
- Apache Commons Lang3 工具类
- Lombok 注解

### 业务特性

- Spring Session Redis 分布式登录
- SaToken 开启路由拦截，使用前后端分离模式，并集成 Redis
- 全局请求响应拦截器（记录日志）
- 全局异常处理器
- 自定义错误码
- 封装通用响应类
- 全局跨域处理
- 长整数丢失精度解决
- 多环境配置


## 业务功能

- 提供示例 SQL（用户表）
- 用户登录、注册、注销、更新、检索、权限管理

### 单元测试

- JUnit5 单元测试
- 示例单元测试类

### 架构设计

- 合理分层


## 快速上手

> 所有需要修改的地方都标记了 `todo`，便于大家找到修改的位置~

### MySQL 数据库

1）修改 `application.yml` 的数据库配置为你自己的：

```yml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/my_db
    username: root
    password: 123456
```

2）执行 `sql/create_table.sql` 中的数据库语句，自动创建库表

### Redis 配置
1）修改 `application.yml` 的 Redis 配置为你自己的：

```yml
redis:
  database: 1
  host: localhost
  port: 6379
  password:
  timeout: 5000
  lettuce:
    pool:
      # 连接池最大连接数
      max-active: 200
      # 连接池最大阻塞等待时间（使用负值表示没有限制）
      max-wait: -1ms
      # 连接池中的最大空闲连接
      max-idle: 10
      # 连接池中的最小空闲连接
      min-idle: 0
```

3）移除 `MainApplication` 类开头 `@SpringBootApplication` 注解内的 exclude 参数：

修改前：

```java
@SpringBootApplication(exclude = {RedisAutoConfiguration.class})
```

修改后：


```java
@SpringBootApplication
```

### Redis 分布式登录

1）Redis 配置，见上

2）修改 `application.yml` 中的 session 存储方式：

```yml
spring:
  session:
    store-type: redis
```



### SaToken 集成 Redis，并使用独立的 Redis

1）Redis 配置，见上（完成 Redis 配置即开启 集成Redis）

2）修改 `application.yml` 中的 alone-redis 配置：

```yml
alone-redis:
  # Redis数据库索引（默认为0）
  database: 2
  # Redis服务器地址
  host: 127.0.0.1
  # Redis服务器连接端口
  port: 6379
  # Redis服务器连接密码（默认为空）
  password:
  # 连接超时时间
  timeout: 5000
```
