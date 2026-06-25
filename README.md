# 校园快递取件优化系统

![Java](https://img.shields.io/badge/Java-11-blue.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7-green.svg)
![Vue.js](https://img.shields.io/badge/Vue.js-3.4-green.svg)
![Element Plus](https://img.shields.io/badge/Element%20Plus-2.4-blue.svg)
![MySQL](https://img.shields.io/badge/MySQL-8.0-orange.svg)

## 📋 项目简介

校园快递取件优化系统是一个基于 Java 和 Vue.js 的 Web 应用，旨在优化校园快递的取件流程，提高效率，减少排队时间。

## 🛠️ 技术栈

### 后端
- **Java**: 11
- **Spring Boot**: 2.7.18
- **MyBatis**: 2.3.2
- **MySQL**: 8.0+
- **JWT**: 0.11.5
- **Lombok**

### 前端
- **Vue.js**: 3.4.0
- **Vue Router**: 4.2.5
- **Pinia**: 2.1.7
- **Axios**: 1.6.2
- **Element Plus**: 2.4.4
- **Vite**: 5.0.10

## 📁 项目结构

```
my-project/
├── backend/                    # 后端 Spring Boot 项目
│   ├── src/main/java/com/example/express/
│   │   ├── config/            # 配置类
│   │   ├── controller/        # 控制器
│   │   ├── service/           # 业务层
│   │   ├── mapper/            # 数据访问
│   │   ├── entity/            # 实体类
│   │   ├── dto/               # 数据传输对象
│   │   ├── util/              # 工具类
│   │   └── exception/         # 异常处理
│   ├── src/main/resources/
│   │   ├── application.yml    # 应用配置
│   │   └── schema.sql         # 数据库初始化脚本
│   └── pom.xml
├── frontend/                   # 前端 Vue 项目
│   ├── src/
│   │   ├── api/               # API 接口封装
│   │   ├── router/            # 路由配置
│   │   ├── store/             # 状态管理
│   │   ├── utils/             # 工具函数
│   │   └── views/             # 页面组件
│   ├── package.json
│   └── vite.config.js
├── 文档/                       # 项目文档
│   ├── docs/                  # 详细文档
│   └── images/                # 文档图片
└── README.md
```

## 🚀 快速开始

### 环境要求

- JDK 11+
- MySQL 8.0+
- Node.js v20+
- Maven 3.8+

### 1. 数据库初始化

```bash
mysql -u root -p123456 < backend/src/main/resources/schema.sql
```

### 2. 后端启动

```bash
cd backend
mvn spring-boot:run
```

后端运行地址：http://localhost:8080

### 3. 前端启动

```bash
cd frontend
npm install
npm run dev
```

前端运行地址：http://localhost:5173

### 4. 测试账号

| 角色 | 账号 | 密码 |
| :--- | :--- | :--- |
| 管理员 | admin | password |
| 学生 | 13800138001 | 123456 |
| 学生 | 13800138002 | 123456 |

## ✨ 功能模块

### 用户端
- ✅ 用户注册与登录
- ✅ 快递查询（按手机号）
- ✅ 取件码一键复制
- ✅ 取件预约系统
- ✅ 消息通知
- ✅ 系统公告

### 管理端
- ✅ 快递信息录入（单条/批量）
- ✅ 快递管理
- ✅ 预约管理
- ✅ 用户管理
- ✅ 公告管理
- ✅ 数据统计

## 📖 项目文档

| 文档 | 说明 |
| :--- | :--- |
| [需求分析文档](文档/需求分析文档.md) | 项目需求分析 |
| [系统设计文档](文档/系统设计文档.md) | 系统架构设计 |
| [数据库设计文档](文档/数据库设计文档.md) | 数据库设计 |
| [开发环境搭建指南](文档/开发环境搭建指南.md) | 环境配置说明 |
| [系统实现文档](文档/docs/系统实现文档.md) | 系统实现详情 |
| [后端核心接口开发文档](文档/docs/后端核心接口开发文档.md) | 接口文档 |
| [前端基础页面开发说明](文档/docs/前端基础页面开发说明.md) | 前端开发说明 |

## 📝 提交规范

```
feat: 新功能
fix: 修复 bug
docs: 文档更新
refactor: 重构
test: 测试相关
chore: 构建/工具相关
```

## 📄 许可证

本项目仅供学习使用

---

**项目版本**：V3.0  
**最后更新**：2026 年 6 月 25 日  
**团队**：极速取件小组
