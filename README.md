# 康培管理后台 (Kangpei Management Backend)

<p align="center">
 <img src="https://img.shields.io/badge/Spring%20Boot-2.7.18-blue.svg" alt="Spring Boot">
 <img src="https://img.shields.io/badge/MySQL-5.7%2F8.0-blue.svg" alt="MySQL">
 <img src="https://img.shields.io/badge/License-MIT-green.svg" alt="License">
</p>

## 📖 项目简介

康培管理后台是为"暖心之光"微信小程序开发的后端管理系统，专注于儿童发展测评与管理。系统提供完整的测评活动管理、问卷管理、结果分析等功能，帮助家长和教育工作者更好地了解儿童的成长发展状况。

### 核心特性

- 🎯 **测评管理**：创建和管理多种类型的儿童发展测评活动
- 📝 **问卷编排**：灵活关联外部问卷系统，构建完整的测评流程
- 📊 **结果分析**：自动汇总测评结果，生成详细的评估报告
- 👶 **宝宝档案**：管理儿童基本信息与成长记录
- 📱 **移动端支持**：为微信小程序提供完整的API接口
- 🔐 **多租户架构**：支持多机构独立管理，数据隔离

## 🎨 功能模块

### 测评管理 (Assessment Management)

测评管理是系统的核心模块，用于创建和管理各类儿童发展测评活动。

**核心功能：**
- ✅ 创建测评活动，定义测评标题、描述、目标人群
- ✅ 选择并关联已发布的问卷，构建测评流程
- ✅ 配置问卷顺序、必填项、权重等参数
- ✅ 管理测评状态：草稿、已发布、已结束、已取消
- ✅ 设置测评时间范围和参与人数限制
- ✅ 支持预约制和开放式两种参与模式
- ✅ 查看测评统计数据和参与情况

**测评类型：**
- 儿童发展测评
- 行为评估
- 认知能力测评
- 情感发展测评
- 社交技能测评

### 问卷管理 (Questionnaire Management)

问卷管理用于维护外部问卷系统的链接信息。问卷的具体配置和内容在外部系统中完成，本系统负责问卷的元数据管理和状态控制。

**核心功能：**
- ✅ 创建问卷记录，存储问卷标题、描述、外部链接
- ✅ 管理问卷状态：草稿、已发布、已下线、已归档
- ✅ 设置问卷类型、目标人群、预计时长
- ✅ 统计问卷访问次数和完成次数
- ✅ 设置问卷有效期（开始时间、结束时间）
- ✅ 提供问卷链接测试功能
- ✅ 支持问卷同步功能，从外部系统同步问卷信息

**问卷类型：**
- 儿童发展问卷
- 行为调查问卷
- 认知测试问卷
- 情感调查问卷
- 社交评估问卷
- 家长反馈问卷
- 教师评估问卷

### 结果管理 (Result Management)

结果管理模块负责收集、存储和展示测评结果，并提供结果分析和报告生成功能。

**核心功能：**
- ✅ 自动创建测评总结果记录
- ✅ 接收并存储单个问卷的完成结果
- ✅ 根据问卷权重自动计算总分和评级
- ✅ 生成测评总结报告（支持文本和PDF格式）
- ✅ 管理员查看所有用户的测评结果
- ✅ 用户查看自己的测评历史和结果详情
- ✅ 支持结果数据导出

**结果生成流程：**
1. 用户开始测评 → 创建测评总结果（状态：进行中）
2. 完成单个问卷 → 创建问卷子结果
3. 完成所有必填问卷 → 计算总分、生成总结报告
4. 更新测评总结果（状态：已完成）

### 内容管理 (Content Management)

**文章管理：**
- ✅ 发布和管理平台文章内容
- ✅ 支持分类、标签管理
- ✅ 文章状态控制（草稿、已发布）

### 系统基础功能

基于 ruoyi-vue-pro 框架，系统内置以下基础功能：

**用户权限管理：**
- 用户管理、角色管理、菜单管理
- 部门管理、岗位管理
- 字典管理、配置管理
- 多租户支持（SaaS架构）

**系统监控：**
- 操作日志、登录日志
- 在线用户监控
- 定时任务管理
- 系统接口文档（Swagger）

**基础设施：**
- 文件上传与存储
- 短信发送功能
- 站内信通知
- 代码生成器

## 🏗️ 技术架构

### 技术栈

| 技术                | 版本      | 说明                  |
|-------------------|---------|---------------------|
| Spring Boot       | 2.7.18  | 应用开发框架              |
| MySQL             | 5.7/8.0 | 关系型数据库              |
| MyBatis Plus      | 3.5.7   | ORM框架               |
| Redis             | 5.0+    | 缓存数据库               |
| Redisson          | 3.32.0  | Redis客户端            |
| Spring Security   | 5.7.11  | 安全认证框架              |
| JWT               | -       | Token认证             |
| Druid             | 1.2.23  | 数据库连接池              |
| Swagger/Springdoc | 1.7.0   | API文档               |
| MapStruct         | 1.6.3   | Bean映射工具            |
| Lombok            | 1.18.34 | 代码简化工具              |
| Hutool            | -       | Java工具类库            |
| JUnit + Mockito   | -       | 单元测试                |

### 模块结构

```
evaluation-project
├── yudao-dependencies        # Maven依赖版本管理
├── yudao-framework           # 框架核心封装
├── yudao-server              # 应用启动入口
├── yudao-module-system       # 系统管理模块
├── yudao-module-infra        # 基础设施模块
├── emojump-evaluation        # 测评业务模块 ⭐️核心模块
│   ├── controller
│   │   ├── admin             # 管理后台接口
│   │   └── app               # 移动端API接口
│   ├── service               # 业务逻辑层
│   ├── dal                   # 数据访问层
│   │   ├── dataobject        # 数据对象
│   │   └── mysql             # MyBatis Mapper
│   ├── convert               # 数据转换
│   ├── enums                 # 枚举类
│   └── framework             # 模块级框架扩展
└── sql                       # 数据库脚本
    └── mysql
        └── emojump-evaluation.sql
```

### 数据库设计

核心数据表：

| 表名                             | 说明          |
|--------------------------------|-------------|
| `emo_questionnaire`            | 问卷表         |
| `emo_assessment`               | 测评表         |
| `emo_assessment_questionnaire` | 测评问卷关联表     |
| `emo_assessment_result`        | 测评结果表       |
| `emo_questionnaire_result`     | 问卷结果表       |
| `emo_questionnaire_access`     | 问卷访问记录表     |
| `emo_article`                  | 文章表         |

**数据表关系：**
- 一个测评可以关联多个问卷（多对多关系，通过 `emo_assessment_questionnaire` 关联）
- 一个测评结果包含多个问卷结果（一对多关系）
- 问卷可以被多个测评复用

## 📡 API接口

系统提供两套API接口：
- **管理后台API**: `/admin-api/emojump/` - 供后台管理使用
- **移动端API**: `/app-api/emojump/` - 供微信小程序使用

### 管理后台接口

#### 测评管理 (Assessment)
```
POST   /admin-api/emojump/assessment/create                创建测评
PUT    /admin-api/emojump/assessment/update                更新测评
DELETE /admin-api/emojump/assessment/delete                删除测评
GET    /admin-api/emojump/assessment/get                   获取测评详情
GET    /admin-api/emojump/assessment/page                  分页查询测评
POST   /admin-api/emojump/assessment/publish               发布测评
POST   /admin-api/emojump/assessment/unpublish             取消发布
GET    /admin-api/emojump/assessment/available-questionnaires  获取可选问卷列表
```

#### 问卷管理 (Questionnaire)
```
POST   /admin-api/emojump/questionnaire/create             创建问卷
PUT    /admin-api/emojump/questionnaire/update             更新问卷
DELETE /admin-api/emojump/questionnaire/delete             删除问卷
GET    /admin-api/emojump/questionnaire/get                获取问卷详情
GET    /admin-api/emojump/questionnaire/page               分页查询问卷
POST   /admin-api/emojump/questionnaire/publish            发布问卷
POST   /admin-api/emojump/questionnaire/unpublish          下线问卷
GET    /admin-api/emojump/questionnaire/published          获取已发布问卷列表
POST   /admin-api/emojump/questionnaire/test-link          测试问卷链接
```

#### 测评结果管理 (Assessment Result)
```
GET    /admin-api/emojump/assessment-result/get            获取测评结果详情
GET    /admin-api/emojump/assessment-result/page           分页查询测评结果
DELETE /admin-api/emojump/assessment-result/delete         删除测评结果
GET    /admin-api/emojump/assessment-result/export         导出测评结果
GET    /admin-api/emojump/assessment-result/pdf            生成PDF报告
```

#### 问卷结果管理 (Questionnaire Result)
```
GET    /admin-api/emojump/questionnaire-result/get         获取问卷结果详情
GET    /admin-api/emojump/questionnaire-result/page        分页查询问卷结果
DELETE /admin-api/emojump/questionnaire-result/delete      删除问卷结果
```

### 移动端API接口

#### 测评相关
```
GET    /app-api/emojump/assessment/get                     获取测评信息
GET    /app-api/emojump/assessment/page                    分页查询测评
GET    /app-api/emojump/assessment/published               获取已发布测评列表
POST   /app-api/emojump/assessment/participate             参与测评
POST   /app-api/emojump/assessment/submit                  提交测评
GET    /app-api/emojump/assessment/my-assessments          获取我的测评列表
GET    /app-api/emojump/assessment/result                  获取测评结果
```

#### 问卷相关
```
GET    /app-api/emojump/questionnaire/get                  获取问卷信息
GET    /app-api/emojump/questionnaire/published            获取已发布问卷列表
GET    /app-api/emojump/questionnaire/access               获取问卷访问链接
POST   /app-api/emojump/questionnaire/record-access        记录问卷访问
GET    /app-api/emojump/questionnaire/popular              获取热门问卷
GET    /app-api/emojump/questionnaire/search               搜索问卷
```

#### 文章相关
```
GET    /app-api/emojump/article/page                       分页查询文章
GET    /app-api/emojump/article/get                        获取文章详情
```

完整的API文档可通过 Swagger UI 访问：`http://localhost:48080/doc.html`

## 🚀 快速开始

### 环境要求

- JDK 8+
- MySQL 5.7 / 8.0+
- Redis 5.0+
- Maven 3.6+

### 安装步骤

#### 1. 克隆项目
```bash
git clone <repository-url>
cd evaluation-project
```

#### 2. 数据库初始化

**创建数据库：**
```sql
CREATE DATABASE ruoyi_vue_pro CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

**执行SQL脚本：**
```bash
# 执行基础框架的SQL脚本（系统表）
sql/mysql/*.sql

# 执行测评模块的SQL脚本
sql/mysql/emojump-evaluation.sql
```

#### 3. 配置文件

修改 `yudao-server/src/main/resources/application-dev.yaml` 配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://127.0.0.1:3306/ruoyi_vue_pro?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&nullCatalogMeansCurrent=true
    username: root
    password: your_password
    
  data:
    redis:
      host: 127.0.0.1
      port: 6379
      password: # Redis密码，如果没有密码可以留空
```

#### 4. 启动项目

**方式一：使用IDE启动**
- 打开 `yudao-server` 模块
- 找到 `YudaoServerApplication` 类
- 右键运行 `main` 方法

**方式二：使用Maven命令**
```bash
cd yudao-server
mvn spring-boot:run
```

#### 5. 访问系统

启动成功后，访问：
- 后端接口：http://localhost:48080
- API文档：http://localhost:48080/doc.html

默认管理员账号：
- 用户名：`admin`
- 密码：`admin123`

### 模块依赖配置

在 `yudao-server` 的 `pom.xml` 中确保包含测评模块依赖：

```xml
<dependency>
    <groupId>cn.iocoder.boot</groupId>
    <artifactId>emojump-evaluation</artifactId>
    <version>${revision}</version>
</dependency>
```

## 📝 使用示例

### 1. 创建问卷

```bash
POST /admin-api/emojump/questionnaire/create
Content-Type: application/json
Authorization: Bearer <your-token>

{
    "title": "3-6岁儿童发展评估问卷",
    "description": "评估3-6岁儿童在认知、语言、社交等方面的发展状况",
    "link": "https://external-survey.com/questionnaire/123",
    "type": 1,
    "targetAudience": "3-6岁儿童家长",
    "estimatedDuration": 15,
    "isOpen": true,
    "validFrom": "2024-01-01T00:00:00",
    "validTo": "2024-12-31T23:59:59"
}
```

### 2. 创建测评活动

```bash
POST /admin-api/emojump/assessment/create
Content-Type: application/json
Authorization: Bearer <your-token>

{
    "title": "2024年春季儿童发展测评",
    "description": "针对3-6岁儿童的综合发展评估活动",
    "type": 1,
    "targetAudience": "3-6岁儿童",
    "duration": 30,
    "startTime": "2024-03-01T09:00:00",
    "endTime": "2024-06-30T18:00:00",
    "needAppointment": true,
    "maxParticipants": 100,
    "isRepeatable": false
}
```

### 3. 关联问卷到测评

```bash
POST /admin-api/emojump/assessment/add-questionnaire
Content-Type: application/json
Authorization: Bearer <your-token>

{
    "assessmentId": 1,
    "questionnaireId": 1,
    "sortOrder": 1,
    "isRequired": true,
    "weight": 1.0
}
```

### 4. 发布测评

```bash
POST /admin-api/emojump/assessment/publish
Content-Type: application/json
Authorization: Bearer <your-token>

{
    "id": 1
}
```

## 🔐 权限配置

在系统管理中需要配置以下权限：

**测评管理权限：**
- `emojump:assessment:create` - 创建测评
- `emojump:assessment:update` - 更新测评
- `emojump:assessment:delete` - 删除测评
- `emojump:assessment:query` - 查询测评
- `emojump:assessment:publish` - 发布测评

**问卷管理权限：**
- `emojump:questionnaire:create` - 创建问卷
- `emojump:questionnaire:update` - 更新问卷
- `emojump:questionnaire:delete` - 删除问卷
- `emojump:questionnaire:query` - 查询问卷
- `emojump:questionnaire:publish` - 发布问卷

**结果管理权限：**
- `emojump:assessment-result:query` - 查询测评结果
- `emojump:assessment-result:delete` - 删除测评结果
- `emojump:assessment-result:export` - 导出测评结果

## 📋 业务流程

### 管理员创建测评流程

1. **创建问卷** → 录入外部问卷的链接和基本信息
2. **发布问卷** → 将问卷状态设为"已发布"
3. **创建测评** → 定义测评活动的基本信息
4. **关联问卷** → 将一个或多个问卷添加到测评中
5. **配置问卷** → 设置问卷顺序、必填项、权重
6. **发布测评** → 将测评对外发布

### 用户参与测评流程

1. **浏览测评** → 在小程序中查看已发布的测评列表
2. **开始测评** → 选择一个测评并开始参与
3. **完成问卷** → 按顺序完成测评中的所有问卷
   - 点击问卷链接跳转到外部系统
   - 在外部系统完成问卷填写
   - 外部系统回调，提交结果到本系统
4. **查看结果** → 完成所有必填问卷后，查看测评总结果

### 结果生成流程

1. 用户开始测评 → 系统创建测评总结果记录（状态：进行中）
2. 用户完成单个问卷 → 外部系统回调 → 创建问卷子结果记录
3. 用户完成所有必填问卷 → 系统自动计算总分
4. 系统根据权重和算法生成总评级和报告
5. 更新测评总结果状态为"已完成"

## ⚠️ 注意事项

1. **外部问卷系统集成**
   - 本系统只管理问卷链接，问卷的具体配置在外部系统完成
   - 需要外部系统提供回调接口，用于提交问卷结果
   - 建议在生产环境对问卷链接进行有效性检查

2. **数据安全**
   - 测评结果涉及儿童隐私，需要严格的权限控制
   - 建议启用HTTPS保护数据传输
   - 定期备份数据库

3. **性能优化**
   - Redis缓存已启用，减少数据库查询
   - 建议对高频访问的接口进行性能监控
   - 大量并发访问时考虑扩展Redis集群

4. **多租户配置**
   - 系统支持SaaS多租户架构
   - 每个租户的数据完全隔离
   - 租户管理员只能管理本租户的数据

## 📚 相关文档

- [测评模块详细文档](emojump-evaluation/README.md)
- [测评模块产品需求文档](emojump-evaluation/emojump-evaluation-prd.md)
- [数据库设计文档](sql/mysql/emojump-evaluation.sql)

## 📮 联系方式

如有问题或建议，欢迎通过以下方式联系：

- 提交 Issue
- 发送邮件

---

**Made with ❤️ for children's development**
