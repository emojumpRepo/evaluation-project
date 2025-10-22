## Why

为了提升宝宝测评系统的用户体验和管理效率，需要增加文件上传和测评报告导出功能。当前系统缺少文件管理能力，管理员无法为宝宝上传相关附件文档，用户无法下载这些附件，同时缺少灵活的报告导出方式。

## What Changes

- **管理后台功能**：
  - 宝宝管理页面新增"附件上传"功能，支持管理员为宝宝上传测评相关文档
  - 管理附件列表，包括查看、删除、更新等操作
  - 关联查看测评数据，便于管理员全面了解宝宝测评情况

- **小程序端功能**：
  - 宝宝管理页面显示可下载的附件列表
  - 附件下载功能，支持下载管理员上传的文件
  - 测评报告导出功能，提供三种下载方式：
    - 单独下载测评报告（PDF格式）
    - 单独下载管理员上传的附件
    - 打包下载报告与附件（ZIP格式）

- **数据存储**：
  - 新增文件关联表，存储上传文件与宝宝、测评结果的关联关系
  - 扩展现有数据库结构以支持文件管理

## Impact

- **Affected specs**:
  - `baby-management` - 宝宝管理功能模块

- **Affected code**:
  - `yudao-module-member/src/main/java/cn/iocoder/yudao/module/member/controller/app/baby/AppMemberBabyController.java` - 小程序端宝宝管理控制器
  - `yudao-module-member/src/main/java/cn/iocoder/yudao/module/member/controller/admin/baby/MemberBabyController.java` - 管理后台宝宝管理控制器
  - `emojump-evaluation/src/main/java/cn/iocoder/yudao/module/emojump/controller/app/assessmentresult/AppAssessmentResultController.java` - 测评结果控制器
  - 新增文件管理和导出相关的服务层和数据访问层代码

- **Database changes**:
  - 新增 `baby_assessment_file` 表用于存储文件关联信息
  - 可能需要扩展现有测评相关表结构

- **Dependencies**:
  - 需要使用现有的文件存储服务 (`yudao-module-infra`)
  - 需要使用现有的PDF生成服务 (`AssessmentResultPdfGenerator`)
  - 可能需要引入ZIP文件处理依赖