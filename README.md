# SpringBoot 项目初始模板

> 作者：shuaigef

基于 Java SpringBoot 的项目初始模板，使用 Spring Security 作为权限校验框架，整合了常用框架和主流业务的示例代码。

## 模板特点

### 主流框架 & 特性

- Spring Boot 2.7.x
- Spring MVC
- MyBatis + MyBatis Plus 数据访问（开启分页）
- Spring AOP 切面编程
- Spring 事务注解
- Spring security + JWT 权限验证
- knife4j + swagger 在线调试接口

### 数据存储

- MySQL 数据库
- Redis 内存数据库

### 工具类

- Hutool 工具库
- Gson 解析库
- Apache Commons Lang3 工具类
- Lombok 注解

### 业务特性

- Spring Security + JWT 接口权限校验
- 全局请求响应拦截器（记录日志）
- 全局异常处理器
- 自定义错误码
- 封装通用响应类
- 全局跨域处理
- 长整数丢失精度解决
- 多环境配置


## 业务功能

- 提供示例 SQL（用户表）
- 用户登录、新增、查询、删除，系统权限管理

### 单元测试

- JUnit5 单元测试
- 示例单元测试类（暂无）

### 架构设计

- 合理分层


## 快速上手

> 所有需要修改的地方都标记了 `todo`，便于大家找到修改的位置~
> 
> Tips: `application.yml` 文件包含了所有配置信息，
> 建议根据不同环境新建配置文件进行配置，多环境配置文件已保存到 `.gitignore` 文件，避免敏感配置信息上传，
> 例如开发环境新建 `application-dev.yml`，在该文件中配置数据库等信息

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

2）移除 `MainApplication` 类开头 `@SpringBootApplication` 注解内的 exclude 参数：

修改前：

```java
@SpringBootApplication(exclude = {RedisAutoConfiguration.class})
```

修改后：


```java
@SpringBootApplication
```

### Spring Security 配置

1）Spring Security 配置 -> `/config/SecurityConfig.java`

2）修改 `SecurityConstant.java` 中需要放行和不做权限校验的请求路径

### Knife4j 配置

1）在 `application.yml` 中开启knife4j

```yml
knife4j:
  enable: true
```

2) 在 `SwaggerConfig.java` 修改 swagger 相关配置信息

### 接口调试

启动 `MainApplication`，打开 `http://localhost:8080/api/doc.html` 调试接口

### 邮箱工具类

复制 `/resources/config` 下的 `mail.setting.template` 文件到同级目录下，改名为`mail.setting`，并在其中进行邮箱配置
