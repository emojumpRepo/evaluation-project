# Emojump 测评模块

## 模块简介

测评模块主要实现宝宝管理、测评管理、问卷管理、轮播图相关功能。其中：
- **测评管理**：用于发布问卷，可以选择已发布的问卷作为收集问卷
- **问卷管理**：管理问卷链接，问卷的具体配置功能在外部系统实现

## 功能特性

### 测评管理
- 创建测评活动
- 选择已发布的问卷作为测评工具
- 管理测评状态（草稿、已发布、已结束）
- 设置测评时间范围和参与人数限制
- 支持预约制和开放式两种参与模式

### 问卷管理
- 创建问卷记录（存储问卷链接）
- 管理问卷状态（草稿、已发布、已下线）
- 统计问卷访问次数和完成次数
- 支持问卷有效期设置
- 提供问卷链接测试功能

### App端功能
- 查看已发布的测评列表
- 参与测评活动
- 查看测评结果
- 热门问卷推荐
- 问卷搜索功能

## 技术架构

### 后端架构
- **Controller层**：提供REST API接口
  - 管理后台：`/admin-api/emojump/`
  - 用户App：`/app-api/emojump/`
- **Service层**：业务逻辑处理
- **Mapper层**：数据持久化
- **Convert层**：数据转换

### 数据库设计
- `emo_questionnaire`：问卷表
- `emo_assessment`：测评表  
- `emo_assessment_result`：测评结果表
- `emo_questionnaire_access`：问卷访问记录表

## API接口

### 管理后台接口

#### 测评管理
```
POST   /admin-api/emojump/assessment/create        创建测评
PUT    /admin-api/emojump/assessment/update        更新测评
DELETE /admin-api/emojump/assessment/delete        删除测评
GET    /admin-api/emojump/assessment/get           获取测评详情
GET    /admin-api/emojump/assessment/list          获取测评列表
POST   /admin-api/emojump/assessment/publish       发布测评
POST   /admin-api/emojump/assessment/unpublish     取消发布测评
GET    /admin-api/emojump/assessment/available-questionnaires  获取可选问卷
```

#### 问卷管理
```
POST   /admin-api/emojump/questionnaire/create     创建问卷
PUT    /admin-api/emojump/questionnaire/update     更新问卷
DELETE /admin-api/emojump/questionnaire/delete     删除问卷
GET    /admin-api/emojump/questionnaire/get        获取问卷详情
GET    /admin-api/emojump/questionnaire/list       获取问卷列表
POST   /admin-api/emojump/questionnaire/publish    发布问卷
POST   /admin-api/emojump/questionnaire/unpublish  下线问卷
GET    /admin-api/emojump/questionnaire/published  获取已发布问卷
POST   /admin-api/emojump/questionnaire/test-link  测试问卷链接
```

### App端接口

#### 测评相关
```
GET    /app-api/emojump/assessment/get             获取测评信息
GET    /app-api/emojump/assessment/list            获取测评列表
GET    /app-api/emojump/assessment/published       获取已发布测评
POST   /app-api/emojump/assessment/participate     参与测评
POST   /app-api/emojump/assessment/submit          提交测评结果
GET    /app-api/emojump/assessment/my-assessments  获取我的测评
GET    /app-api/emojump/assessment/result          获取测评结果
```

#### 问卷相关
```
GET    /app-api/emojump/questionnaire/get          获取问卷信息
GET    /app-api/emojump/questionnaire/published    获取已发布问卷
GET    /app-api/emojump/questionnaire/access       获取问卷访问链接
POST   /app-api/emojump/questionnaire/record-access 记录问卷访问
GET    /app-api/emojump/questionnaire/popular      获取热门问卷
GET    /app-api/emojump/questionnaire/search       搜索问卷
```

## 数据模型

### 测评类型枚举
- `CHILD_DEVELOPMENT(1, "儿童发展测评")`
- `BEHAVIOR_EVALUATION(2, "行为评估")`
- `COGNITIVE_ASSESSMENT(3, "认知能力测评")`
- `EMOTIONAL_ASSESSMENT(4, "情感发展测评")`
- `SOCIAL_SKILLS(5, "社交技能测评")`

### 测评状态枚举
- `DRAFT(0, "草稿")`
- `PUBLISHED(1, "已发布")`
- `ENDED(2, "已结束")`
- `CANCELLED(3, "已取消")`

### 问卷类型枚举
- `CHILD_DEVELOPMENT(1, "儿童发展问卷")`
- `BEHAVIOR_SURVEY(2, "行为调查问卷")`
- `COGNITIVE_TEST(3, "认知测试问卷")`
- `EMOTIONAL_SURVEY(4, "情感调查问卷")`
- `SOCIAL_ASSESSMENT(5, "社交评估问卷")`
- `PARENT_FEEDBACK(6, "家长反馈问卷")`
- `TEACHER_EVALUATION(7, "教师评估问卷")`

### 问卷状态枚举
- `DRAFT(0, "草稿")`
- `PUBLISHED(1, "已发布")`
- `OFFLINE(2, "已下线")`
- `ARCHIVED(3, "已归档")`

## 部署说明

### 1. 数据库初始化
执行 `sql/mysql/emojump-evaluation.sql` 文件创建相关表结构。

### 2. 模块依赖
确保 `yudao-server` 的 `pom.xml` 中已添加以下依赖：
```xml
<dependency>
    <groupId>cn.iocoder.boot</groupId>
    <artifactId>emojump-evaluation</artifactId>
    <version>${revision}</version>
</dependency>
```

### 3. 权限配置
在系统管理中配置以下权限：
- `emojump:assessment:create` - 创建测评
- `emojump:assessment:update` - 更新测评
- `emojump:assessment:delete` - 删除测评
- `emojump:assessment:query` - 查询测评
- `emojump:assessment:publish` - 发布测评
- `emojump:questionnaire:create` - 创建问卷
- `emojump:questionnaire:update` - 更新问卷
- `emojump:questionnaire:delete` - 删除问卷
- `emojump:questionnaire:query` - 查询问卷
- `emojump:questionnaire:publish` - 发布问卷

## 使用示例

### 创建问卷
```bash
POST /admin-api/emojump/questionnaire/create
{
    "title": "儿童发展测评问卷",
    "description": "评估3-6岁儿童发展状况",
    "link": "https://example.com/questionnaire/123",
    "type": 1,
    "targetAudience": "3-6岁儿童",
    "estimatedDuration": 15,
    "isOpen": true,
    "validFrom": "2024-01-01T00:00:00",
    "validTo": "2024-12-31T23:59:59"
}
```

### 创建测评
```bash
POST /admin-api/emojump/assessment/create
{
    "title": "春季儿童发展测评",
    "description": "2024年春季测评活动",
    "questionnaireId": 1,
    "type": 1,
    "targetAudience": "3-6岁儿童",
    "duration": 30,
    "startTime": "2024-01-01T09:00:00",
    "endTime": "2024-06-30T18:00:00",
    "needAppointment": true,
    "maxParticipants": 100
}
```

## 注意事项

1. 问卷的具体配置功能在外部系统实现，本模块只管理问卷链接
2. 测评结果的详细分析需要结合外部问卷系统的数据
3. 用户相关功能（如我的测评）需要用户系统的支持
4. 建议在生产环境中对问卷链接进行有效性检查 
