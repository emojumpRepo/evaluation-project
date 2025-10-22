## ADDED Requirements

### Requirement: 管理员附件上传功能
管理员 SHALL 可以为宝宝上传测评相关的附件文档，如医疗报告、成长记录等文档。

#### Scenario: 管理员成功上传附件
- **WHEN** 管理员在管理后台选择文件并上传，指定关联的宝宝信息
- **THEN** 系统 SHALL 验证管理员具有附件上传权限
- **THEN** 系统 SHALL 验证文件类型和大小符合要求
- **THEN** 系统 SHALL 将文件存储到文件系统并生成文件记录
- **THEN** 系统 SHALL 在 `baby_assessment_file` 表中创建关联记录
- **THEN** 系统 SHALL 记录管理员的上传操作日志
- **THEN** 系统 SHALL 返回上传成功的响应，包含文件ID和基本信息

#### Scenario: 管理员附件上传失败
- **WHEN** 管理员上传的文件类型不被支持
- **THEN** 系统 SHALL 返回明确的错误信息，说明支持的文件类型
- **WHEN** 管理员上传的文件大小超过限制
- **THEN** 系统 SHALL 返回文件大小超限的错误信息
- **WHEN** 管理员上传附件时指定的宝宝信息不存在
- **THEN** 系统 SHALL 返回宝宝信息不存在的错误信息
- **WHEN** 非管理员用户尝试上传附件
- **THEN** 系统 SHALL 拒绝访问并返回权限不足的错误信息

### Requirement: 小程序端附件查看与下载功能
用户 SHALL 可以在小程序端查看和下载管理员上传的附件文档。

#### Scenario: 查看附件列表
- **WHEN** 用户在小程序端进入宝宝的附件页面
- **THEN** 系统 SHALL 显示该宝宝相关的所有附件列表
- **THEN** 列表 SHALL 包含附件名称、上传时间、文件大小等信息
- **THEN** 系统 SHALL 验证用户是该宝宝的家长

#### Scenario: 下载附件
- **WHEN** 用户点击某个附件进行下载
- **THEN** 系统 SHALL 验证用户是否有权限下载该附件
- **THEN** 系统 SHALL 返回原始附件文件供用户下载
- **THEN** 系统 SHALL 记录用户下载操作日志

### Requirement: 测评报告导出功能
用户 SHALL 可以导出测评报告，支持单独下载报告、单独下载附件、打包下载三种方式。

#### Scenario: 单独下载测评报告
- **WHEN** 用户选择"单独下载报告"选项
- **THEN** 系统 SHALL 根据测评结果生成PDF格式的报告文件
- **THEN** 系统 SHALL 返回PDF文件的下载链接或直接返回文件流
- **THEN** 报告 SHALL 包含宝宝基本信息、测评题目、得分结果和建议内容

#### Scenario: 单独下载附件
- **WHEN** 用户选择"单独下载附件"选项，并选择具体的附件
- **THEN** 系统 SHALL 验证用户是否有权限访问该附件
- **THEN** 系统 SHALL 返回用户选择的管理员上传的附件
- **THEN** 系统 SHALL 记录附件下载日志

#### Scenario: 打包下载报告与附件
- **WHEN** 用户选择"打包下载"选项
- **THEN** 系统 SHALL 生成测评报告PDF文件
- **THEN** 系统 SHALL 收集该测评相关的所有管理员上传的附件
- **THEN** 系统 SHALL 将报告和附件打包成一个ZIP文件
- **THEN** 系统 SHALL 返回ZIP文件的下载链接或直接返回文件流

### Requirement: 管理后台附件管理功能
管理员 SHALL 可以在管理后台查看、管理上传的附件内容，并关联查看测评数据。

#### Scenario: 查看附件列表
- **WHEN** 管理员在宝宝管理页面查看某个宝宝的信息
- **THEN** 系统 SHALL 显示该宝宝相关的所有附件列表
- **THEN** 列表 SHALL 包含附件名、上传时间、文件大小、上传管理员等信息
- **THEN** 管理员可以按时间、文件类型等条件进行筛选和排序

#### Scenario: 查看附件内容
- **WHEN** 管理员点击附件列表中的某个附件
- **THEN** 系统 SHALL 根据附件类型提供预览功能
- **THEN** 对于图片附件，系统 SHALL 直接显示图片
- **THEN** 对于PDF文档，系统 SHALL 提供PDF预览
- **THEN** 对于其他类型附件，系统 SHALL 提供下载选项

#### Scenario: 删除附件
- **WHEN** 管理员选择删除某个附件
- **THEN** 系统 SHALL 验证管理员具有删除权限
- **THEN** 系统 SHALL 删除附件记录和物理文件
- **THEN** 系统 SHALL 记录管理员删除操作日志

#### Scenario: 关联查看测评数据
- **WHEN** 管理员查看某个附件时
- **THEN** 系统 SHALL 显示该附件关联的测评信息
- **THEN** 系统 SHALL 提供跳转到对应测评结果页面的链接
- **THEN** 系统 SHALL 显示测评完成时间、得分等关键信息

### Requirement: 附件权限和安全控制
系统 SHALL 必须确保附件访问的安全性，防止未授权访问和数据泄露。

#### Scenario: 用户下载权限验证
- **WHEN** 用户尝试下载附件
- **THEN** 系统 SHALL 验证该用户是否为附件关联宝宝的家长
- **WHEN** 非授权用户尝试下载附件
- **THEN** 系统 SHALL 拒绝访问并返回权限不足的错误信息

#### Scenario: 附件安全检查
- **WHEN** 管理员上传附件时
- **THEN** 系统 SHALL 检查附件类型是否在允许列表中
- **THEN** 系统 SHALL 验证附件大小不超过设定限制
- **THEN** 系统 SHALL 对附件进行病毒扫描（如果配置了相关服务）

#### Scenario: 管理员权限控制
- **WHEN** 管理员尝试上传或管理附件
- **THEN** 系统 SHALL 验证管理员具有相应的管理权限
- **THEN** 系统 SHALL 记录管理员的所有附件操作日志
- **THEN** 系统 SHALL 防止管理员访问其他管理员的未授权附件

### Requirement: 附件管理功能
管理员 SHALL 可以对上传的附件进行完整的管理操作。

#### Scenario: 附件信息查询
- **WHEN** 管理员查询某个宝宝的附件列表
- **THEN** 系统 SHALL 返回该宝宝相关的所有附件信息
- **THEN** 系统 SHALL 支持按附件名、上传时间等条件筛选
- **THEN** 系统 SHALL 支持分页查询以提升性能

#### Scenario: 附件更新
- **WHEN** 管理员需要更新附件信息
- **THEN** 系统 SHALL 允许修改附件的描述或关联信息
- **THEN** 系统 SHALL 记录更新操作日志
- **THEN** 系统 SHALL 验证管理员具有修改权限

### Requirement: 批量操作支持
系统 SHALL 支持对多个附件进行批量操作，提升操作效率。

#### Scenario: 批量下载
- **WHEN** 用户选择多个附件进行批量下载
- **THEN** 系统 SHALL 将选中的附件打包成ZIP格式
- **THEN** 系统 SHALL 返回ZIP文件供用户下载
- **THEN** 系统 SHALL 限制批量下载的附件数量和总大小

#### Scenario: 批量删除
- **WHEN** 管理员选择多个附件进行批量删除
- **THEN** 系统 SHALL 逐个验证每个附件的删除权限
- **THEN** 系统 SHALL 删除所有有权限的附件
- **THEN** 系统 SHALL 记录批量删除操作日志
- **THEN** 系统 SHALL 返回操作结果，说明成功删除和失败的附件数量

#### Scenario: 批量上传
- **WHEN** 管理员需要为多个宝宝批量上传附件
- **THEN** 系统 SHALL 支持批量文件上传功能
- **THEN** 系统 SHALL 逐个验证每个附件的类型和大小
- **THEN** 系统 SHALL 记录批量上传操作日志
- **THEN** 系统 SHALL 返回详细的批量上传结果报告