# 固定资产管理系统 (Fixed Asset Management System)

[![Java](https://img.shields.io/badge/Java-17-orange)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green)](https://spring.io/projects/spring-boot)
[![Vue](https://img.shields.io/badge/Vue.js-3.x-4FC08D)](https://vuejs.org/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.x-blue)](https://www.typescriptlang.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1)](https://www.mysql.com/)
[![Element Plus](https://img.shields.io/badge/Element%20Plus-2.x-409EFF)](https://element-plus.org/)

> **一个现代化、全栈式的高校固定资产管理系统，旨在实现对建筑、房产、器材、车辆等资产的全生命周期数字化管理。**

---

## 📖 项目介绍

本系统解决了传统资产管理中“账实不符”、“利用率低”、“流程繁琐”等核心痛点。采用现代化的 **Spring Boot + Vue 3** 前后端分离架构，实现了从**资产入库建档、日常借用流转、维修报废**到**盘点核算**的全流程闭环管理。

系统内置了完善的 **RBAC (基于角色的访问控制)** 权限模型，支持后勤、安保、车辆管理、系统管理员等多角色协同工作，界面友好，功能完备。

---

## 💡 系统能做什么？(解决方案)

本系统为高校及企事业单位提供了一站式的资产管理解决方案：

*   **全生命周期管理**：从资产采购入库、日常借用、维修保养、一直到报废处置，每一个环节都有据可查，彻底告别“糊涂账”。
*   **多角色协同办公**：
    *   **普通师生**：可以在线申请借用器材、查询空闲教室，无需线下跑腿填单。
    *   **后勤人员**：在线审批借用申请，实时掌握库存状态，一键导出资产报表。
    *   **安保/车辆管理**：大型活动安保报备、公务车辆调度管理，井井有条。
    *   **管理决策层**：通过数据大屏（Dashboard）实时监控资产总值、折旧情况及利用率，辅助科学决策。
*   **智能化盘点**：支持创建定期盘点计划，自动生成**盘盈盘亏差异报告**，极大降低了人工盘点的错误率和时间成本。
*   **资源最大化利用**：通过空闲教室查询算法和可视化图表，帮助管理者合理分配资源，避免闲置浪费。

---

## ✨ 核心功能

### 1. 🏢 档案管理
- **多维资产建档**：覆盖**建筑、房间、器材、车辆**四大核心资产，字段详尽。
- **批量处理**：支持 Excel/CSV 格式的大批量资产数据一键导入与导出。
- **数据关联**：实现了严格的数据约束（如：新增房间必须关联已存在的建筑），确保数据质量。

### 2. 🔄 业务流转
- **器材借用**：包含校内/校外借用申请、多级审批（部门->教务）、归还核销、费用（租金/押金/赔偿）自动计算。
- **智能查询**：基于时间片算法的**空闲教室查询**，支持按容量、时段、楼栋、日期精准检索空闲资源。
- **资源分配**：教职工办公用房申请与分配、学生/教职工宿舍管理。
- **车辆与安保**：车辆调度记录、大型活动安保报备流程。

### 3. 📊 盘点与核算
- **盘点计划**：可自定义盘点范围（按资产类型）与时间，实时录入实物盘点结果。
- **差异报告**：系统自动比对账面数量与实物数量，生成盘盈盘亏差异报告。
- **价值管理**：全生命周期追踪资产原值、净值、折旧及实物状态（正常/损坏/丢失）。

### 4. 🛡️ 系统管理
- **权限控制**：细粒度的权限管理，不同角色登录可看到不同的菜单和路由（动态路由）。
- **数据安全**：采用 JWT Token 认证，后端接口级安全拦截，密码加密存储。

---

## 🛠 技术栈

### 后端 (Backend)
| 技术 | 说明 |
|:---|:---|
| **Spring Boot 3.x** | 核心应用框架 |
| **Java 17** | 开发语言 (JDK 17) |
| **MyBatis-Plus** | ORM 数据库框架 |
| **MySQL 8.0** | 关系型数据库 |
| **Spring Security + JWT** | 认证与授权 |
| **EasyExcel** | Excel 导入导出工具 |
| **SpringDoc (Swagger)** | API 接口文档 |

### 前端 (Frontend)
| 技术 | 说明 |
|:---|:---|
| **Vue 3** | 渐进式 JavaScript 框架 (Composition API) |
| **TypeScript** | 静态类型检查 |
| **Vite** | 新一代前端构建工具 |
| **Element Plus** | UI 组件库 |
| **Pinia** | 状态管理 |
| **Vue Router** | 路由管理 |
| **ECharts** | 数据可视化图表 |

---

## 📂 目录结构

```text
FixedAssetManagementSystem/
├── FixedAssetManagement/        # 后端工程根目录 (Spring Boot)
│   ├── src/main/java/ztt/       # Java 源代码
│   │   ├── controller/          # 控制层 (API 接口)
│   │   ├── service/             # 业务逻辑层
│   │   ├── mapper/              # 数据访问层
│   │   └── entity/              # 实体类
│   └── src/main/resources/      # 配置文件 (application.yml) 及 Mapper XML
│
├── frontend/                    # Frontend工程根目录 (Vue 3)
│   ├── fixed-asset-frontend/
│   │   ├── src/
│   │   │   ├── api/             # API 接口封装
│   │   │   ├── views/           # 页面组件 (档案、业务、盘点等)
│   │   │   ├── components/      # 公共组件
│   │   │   ├── router/          # 路由配置
│   │   │   └── stores/          # Pinia 状态管理
│
├── info (1).sql                 # 主数据库结构及初始化数据
├── 各角色信息.sql                # 角色与用户数据脚本
└── README.md                    # 项目说明文档
```

---

## 🚀 快速开始

### 环境要求
- **Java**: JDK 17+
- **Node.js**: 18.x+ (推荐 v20+)
- **Database**: MySQL 8.0+
- **IDE**: IntelliJ IDEA / VS Code

### 1. 数据库配置
1. 创建一个名为 `fixed_asset` 的数据库（或根据 `application.yml` 配置修改）。
2. 导入根目录下的 SQL 脚本，建议顺序：
   - 先导入 `info (1).sql` (包含表结构和基础数据)。
   - 再导入 `各角色信息.sql` (包含用户和角色数据)。
   - 其他 `.sql` 文件为补充建表信息，已包含在上述脚本中或根据需要导入。
3. 检查后端配置文件中的数据库账号密码是否正确。
4. 还有其他模块系统相关数据库也有使用，这里不完全展示。

### 2. 后端启动
```bash
cd FixedAssetManagement
# 运行 Spring Boot 应用
./mvnw spring-boot:run
```
> 后端服务默认运行在 `http://localhost:8080`

### 3. 前端启动
```bash
cd frontend/fixed-asset-frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```
> 前端页面默认运行在 `http://localhost:5173`

---

## 🔑 默认测试账号

| 角色 | 用户名 | 密码 | 权限说明 |
|:---|:---|:---|:---|
| **系统管理员** | `admin` | `123456` | 拥有系统最高权限，可管理所有模块 |
| **后勤管理员** | `logistics`| `123456` | 负责资产建档、借用审批、维修报废等 |
| **车辆管理员** | `vehicle` | `123456` | 负责车辆档案及车辆调度管理 |
| **安保管理员** | `security` | `123456` | 负责大型活动安保报备 |
| **普通用户（学生&教室）** | `user` | `123456` | 可发起借用申请、查询空闲教室 |

---

## 📄 许可证
本项目采用 [MIT License](LICENSE) 开源许可证。

---

*Created by [LYY/ZTT]*
