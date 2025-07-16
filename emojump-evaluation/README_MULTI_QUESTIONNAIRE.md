# 多问卷测评功能说明

## 功能概述

现在测评管理支持一个测评包含多个问卷的场景。例如：
- **入学常规测评**：包含焦虑量表(SAS) + SCL-90量表
- **期中心理健康评估**：包含焦虑量表 + SCL-90量表 + 抑郁量表(SDS)

## 数据库设计

### 1. 测评表 (emo_assessment)
- 移除了 `questionnaire_id` 字段
- 通过关联表支持多问卷

### 2. 测评问卷关联表 (emo_assessment_questionnaire)
```sql
CREATE TABLE `emo_assessment_questionnaire` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `assessment_id` bigint NOT NULL COMMENT '测评ID',
  `questionnaire_id` bigint NOT NULL COMMENT '问卷ID',
  `sort_order` int DEFAULT 0 COMMENT '排序顺序',
  `is_required` bit(1) DEFAULT b'1' COMMENT '是否必填',
  `weight` decimal(5,2) DEFAULT 1.00 COMMENT '权重（用于计算总分）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_assessment_questionnaire` (`assessment_id`, `questionnaire_id`)
);
```

### 3. 测评结果表优化
- `emo_assessment_result`: 存储整体测评结果
- `emo_questionnaire_result`: 存储单个问卷结果

## API 变化

### 创建测评 API
```json
{
  "title": "入学常规测评",
  "description": "新生入学心理健康常规测评",
  "questionnaires": [
    {
      "questionnaireId": 1,
      "sortOrder": 1,
      "isRequired": true,
      "weight": 0.4
    },
    {
      "questionnaireId": 2,
      "sortOrder": 2,
      "isRequired": true,
      "weight": 0.6
    }
  ],
  "type": 4,
  "targetAudience": "新入学学生"
}
```

### 测评详情响应
```json
{
  "id": 1,
  "title": "入学常规测评",
  "questionnaires": [
    {
      "questionnaireId": 1,
      "questionnaireTitle": "焦虑量表(SAS)",
      "questionnaireDescription": "用于评估焦虑水平的标准量表",
      "sortOrder": 1,
      "isRequired": true,
      "weight": 0.4,
      "estimatedDuration": 10
    },
    {
      "questionnaireId": 2,
      "questionnaireTitle": "SCL-90量表",
      "questionnaireDescription": "症状自评量表，用于心理健康筛查",
      "sortOrder": 2,
      "isRequired": true,
      "weight": 0.6,
      "estimatedDuration": 15
    }
  ]
}
```

### App端参与测评响应
```json
{
  "assessmentId": 1,
  "assessmentTitle": "入学常规测评",
  "questionnaires": [
    {
      "questionnaireId": 1,
      "questionnaireTitle": "焦虑量表(SAS)",
      "questionnaireLink": "https://example.com/questionnaire/sas",
      "sortOrder": 1,
      "isRequired": true,
      "estimatedDuration": 10,
      "isCompleted": false
    },
    {
      "questionnaireId": 2,
      "questionnaireTitle": "SCL-90量表",
      "questionnaireLink": "https://example.com/questionnaire/scl90",
      "sortOrder": 2,
      "isRequired": true,
      "estimatedDuration": 15,
      "isCompleted": false
    }
  ],
  "accessToken": "abc123"
}
```

## 示例数据

数据库中已包含示例数据：

1. **入学常规测评** (ID: 1)
   - 焦虑量表(SAS) - 权重40%
   - SCL-90量表 - 权重60%

2. **期中心理健康评估** (ID: 2)
   - 焦虑量表(SAS) - 权重30%
   - SCL-90量表 - 权重40%
   - 抑郁量表(SDS) - 权重30%

3. **儿童发展专项测评** (ID: 3)
   - 儿童发展问卷 - 权重100%

## 使用场景

### 学校心理健康测评
- 新生入学：焦虑 + 心理健康筛查
- 期中评估：焦虑 + 抑郁 + 心理健康筛查
- 毕业评估：压力 + 适应性 + 职业规划

### 医疗机构评估
- 综合心理评估：多个专业量表组合
- 专项评估：针对特定症状的量表组合

## 技术实现

### 核心类
- `AssessmentQuestionnaireDO`: 测评问卷关联实体
- `AssessmentQuestionnaireMapper`: 关联数据访问层
- `AssessmentCreateReqVO.AssessmentQuestionnaireReqVO`: 问卷配置请求对象
- `AssessmentRespVO.AssessmentQuestionnaireRespVO`: 问卷信息响应对象

### 事务处理
- 创建/更新测评时使用 `@Transactional` 确保数据一致性
- 先操作主表，再操作关联表

### 数据填充
- 查询时自动填充问卷详细信息
- 支持按排序顺序展示问卷列表
- 计算总预计时长等聚合信息
