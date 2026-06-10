# 校园快递取件优化系统

## 项目简介

校园快递取件优化系统是一个基于Java和Vue.js的Web应用，旨在优化校园快递的取件流程，提高效率，减少排队时间。

## 技术栈

### 后端
- **Java**: 11.0.0.2
- **Spring Boot**: 3.2.0
- **MyBatis**: 3.0.3
- **MySQL**: 8.0.44
- **JWT**: 0.12.3
- **Swagger**: 2.3.0

### 前端
- **Vue.js**: 3.4.0
- **Vue Router**: 4.2.5
- **Pinia**: 2.1.7
- **Axios**: 1.6.2
- **Element Plus**: 2.4.4
- **Vite**: 5.0.10

### 其他
- **Maven**: 3.9.9
- **Node.js**: v22.15.0
- **npm**: 11.6.0

## 团队成员

| 姓名 | 角色 | 分工 |
| :--- | :--- | :--- |
| 冯亮都 | 项目经理(PM) | 项目协调、进度管理 |
| 刘帮富 | 架构师/后端 | 后端架构设计、接口开发 |
| 翁立涛 | 前端开发 | 前端页面开发、组件封装 |
| 周兴勃 | 质量保证(QA) | 测试、缺陷跟踪 |

## 项目结构

```
d:\a rj project/
├── backend/                    # 后端Spring Boot项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/express/
│   │   │   │   ├── config/      # 配置类
│   │   │   │   ├── controller/  # 控制器
│   │   │   │   ├── service/     # 业务层
│   │   │   │   ├── mapper/      # 数据访问
│   │   │   │   ├── entity/      # 实体类
│   │   │   │   ├── dto/         # 数据传输对象
│   │   │   │   ├── util/        # 工具类
│   │   │   │   └── exception/   # 异常处理
│   │   │   └── resources/
│   │   │       ├── application.yml
│   │   │       └── schema.sql
│   │   └── test/
│   └── pom.xml
├── frontend/                   # 前端Vue项目
│   ├── src/
│   │   ├── api/               # API接口
│   │   ├── router/           # 路由配置
│   │   ├── store/            # 状态管理
│   │   ├── utils/            # 工具函数
│   │   └── views/            # 页面组件
│   ├── package.json
│   └── vite.config.js
├── docs/                      # 文档目录
│   ├── 后端核心接口开发文档.md
│   ├── 前端基础页面开发说明.md
│   └── 第三周项目进度与测试报告.md
└── README.md
```

## 项目进度里程碑

| 阶段 | 时间 | 目标 | 状态 |
| :--- | :--- | :--- | :--- |
| 需求分析与设计 | 第 1 周 | 完成需求文档、架构设计、UI 原型 | ✅ 已完成 |
| 系统设计 | 第 2 周 | 完成详细设计、数据库设计、环境搭建指南 | ✅ 已完成 |
| 核心功能开发 | 第 3 周 | 完成用户认证、快递管理、预约系统编码 | ✅ 已完成 |
| 功能完善 | 第 4 周 | 扫码取件、短信通知、用户体验优化 | 📋 进行中 |
| 系统测试 | 第 5 周 | 集成测试、性能优化、文档完善 | 📋 待开始 |
| 部署上线 | 第 6 周 | 部署准备、用户培训、项目验收 | 📋 待开始 |

## 功能模块

### Must-have（已完成）
- ✅ 用户注册与登录
- ✅ JWT身份认证
- ✅ 快递信息录入
- ✅ 快递查询（按手机号）
- ✅ 取件预约系统
- ✅ 预约取消
- ✅ 消息通知
- ✅ 管理员后台
- ✅ 数据统计

### Nice-to-have（规划中）
- 📋 代取件功能
- 📋 短信通知
- 📋 扫码取件
- 📋 历史记录

## 快速开始

### 环境要求

- JDK 11.0.0.2+
- MySQL 8.0+
- Node.js v22.15.0+
- Maven 3.9.9+

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
Swagger文档：http://localhost:8080/swagger-ui.html

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
| 学生 | 13800138003 | 123456 |

## 开发规范

### 分支管理
- `main`: 主分支，稳定版本
- `develop`: 开发分支
- `feature/*`: 功能分支

### 提交规范
```
feat: 新功能
fix: 修复bug
docs: 文档更新
refactor: 重构
test: 测试相关
chore: 构建/工具相关
```

## 数据库配置

```yaml
# backend/src/main/resources/application.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/campus_express
    username: root
    password: 123456
```

## API文档

详见 [后端核心接口开发文档](docs/后端核心接口开发文档.md)

## 前端开发说明

详见 [前端基础页面开发说明](docs/前端基础页面开发说明.md)

## 测试报告

详见 [第三周项目进度与测试报告](docs/第三周项目进度与测试报告.md)

## 许可证

本项目仅供学习

---

**项目版本**：V3.0  
**最后更新**：2026 年 6 月 10 日  
**团队**：极速取件小组
