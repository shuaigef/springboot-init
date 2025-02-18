# 数据库初始化

-- 创建库
create database if not exists my_db;

-- 切换库
use my_db;

-- 用户表
create table if not exists sys_user
(
    id            bigint auto_increment comment 'id' primary key,
    username      varchar(256)                           null comment '用户名',
    password varchar(512)                           null comment '密码',
    nickname      varchar(256)                           null comment '昵称',
    user_avatar   varchar(1024)                          null comment '用户头像',
    user_profile  varchar(512)                           null comment '用户简介',
    gender        tinyint                                null comment '性别',
    email         varchar(256)                           null comment '邮箱',
    phone_number  varchar(32)                            null comment '手机号',
    role_id       bigint                                 null comment '角色id',
    create_time   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete     tinyint      default 0                 not null comment '是否删除'
) comment '用户表' collate = utf8mb4_unicode_ci;
insert into `sys_user` (`id`, `username`, `password`, `nickname`, `gender`, `role_id`)
values (1, 'admin','$2a$10$mvEZKI9k.STo6lczs8CsW..yO1Kh6u5J/1EoeBLNTvTCBpJM7Htli', '管理员', 0, 1);


-- 角色表
create table if not exists sys_role
(
    id         bigint auto_increment comment 'id' primary key,
    role_name    varchar(64)                            null comment '角色名称',
    role_desc    varchar(256)                           null comment '角色描述',
    create_time  datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time  datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete    tinyint      default 0                 not null comment '是否删除'
) comment '角色表' collate = utf8mb4_unicode_ci;
insert into `sys_role` (`id`, `role_name`, `role_desc`) values (1, '超级管理员','超级管理员');
insert into `sys_role` (`id`, `role_name`, `role_desc`) values (2, '普通用户','普通用户');

-- 切换库
use my_db;
-- 权限表
create table if not exists sys_authority
(
    id             bigint auto_increment comment 'id' primary key,
    code           varchar(64)                            null comment '权限标识符',
    name           varchar(64)                            null comment '权限名称',
    order_no       int                                    null comment '菜单顺序',
    parent_id      bigint                                 null comment '父节点id',
    authority_type varchar(10)                            null comment '权限类型 menu/button',
    redirect       varchar(128)                           null comment '一级菜单跳转地址',
    path     varchar(128)                                 null comment '路由路径',
    hidden         tinyint                                null comment '是否隐藏路由菜单(0 - 否，1 - 是)',
    menu_icon      varchar(64)                            null comment '菜单图标',
    component      varchar(128)                           null comment '组件',
    component_name varchar(64)                            null comment '组件名称',
    create_time    datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time    datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
) comment '权限表' collate = utf8mb4_unicode_ci;
insert into `sys_authority` (`id`, `code`, `name`, `order_no`, `parent_id`, `authority_type`, `redirect`, `path`, `hidden`, `menu_icon`, `component`, `component_name`)
values (1, 'home', '首页', 1, 0, 'menu', NULL, '/home', 0, 'icon-list', NULL, 'Home');
insert into `sys_authority` (`id`, `code`, `name`, `order_no`, `parent_id`, `authority_type`, `redirect`, `path`, `hidden`, `menu_icon`, `component`, `component_name`)
values (2, 'accountSetting', '账号设置', 2, 0, 'menu', NULL, '/account/setting', 0, 'icon-account-settings', '', 'AccountSetting');
insert into `sys_authority` (`id`, `code`, `name`, `order_no`, `parent_id`, `authority_type`, `redirect`, `path`, `hidden`, `menu_icon`, `component`, `component_name`)
values (3, 'systemManage', '系统管理', 4, 0, 'menu', '/system/manage/sysUser', '/system/manage', 0, 'icon-setting', NULL, 'RouteView');

insert into `sys_authority` (`id`, `code`, `name`, `order_no`, `parent_id`, `authority_type`, `redirect`, `path`, `hidden`, `menu_icon`, `component`, `component_name`)
values (4, 'systemManage:userManage', '用户管理', 1, 3, 'menu', NULL,  '/system/manage/sysUser', 0, NULL, NULL, 'UserManage');
insert into `sys_authority` (`id`, `code`, `name`, `order_no`, `parent_id`, `authority_type`, `redirect`, `path`, `hidden`, `menu_icon`, `component`, `component_name`)
values (5, 'systemManage:roleManage', '角色管理', 2, 3, 'menu', NULL, '/system/manage/sysRole', 0, NULL, NULL, 'RoleManage');
insert into `sys_authority` (`id`, `code`, `name`, `order_no`, `parent_id`, `authority_type`, `redirect`, `path`, `hidden`, `menu_icon`, `component`, `component_name`)
values (6, 'systemManage:authorityManage', '权限管理', 3, 3, 'menu', NULL, '/system/manage/sysAuthority', 0, NULL, NULL, 'AuthorityManage');

-- 角色权限表
create table if not exists sys_role_authority
(
    id             bigint auto_increment comment 'id' primary key,
    authority_id   bigint                                 null comment '权限id',
    role_id        bigint                                 null comment '角色id',
    create_time    datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time    datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
) comment '角色权限表' collate = utf8mb4_unicode_ci;
insert into `sys_role_authority` (`id`, `authority_id`, `role_id`) values (1, 1, 1);
insert into `sys_role_authority` (`id`, `authority_id`, `role_id`) values (2, 2, 1);
insert into `sys_role_authority` (`id`, `authority_id`, `role_id`) values (3, 3, 1);
insert into `sys_role_authority` (`id`, `authority_id`, `role_id`) values (4, 4, 1);
insert into `sys_role_authority` (`id`, `authority_id`, `role_id`) values (5, 5, 1);
insert into `sys_role_authority` (`id`, `authority_id`, `role_id`) values (6, 6, 1);

