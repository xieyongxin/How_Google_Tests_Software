# 2.1.2–2.1.8 SET 基础职责与项目早期阶段

本实验把书中关于 SET 角色、项目早期投入、团队结构、设计文档、自动化计划和可测试性的内容，转化为一个最小的 AddUrl 概念验证项目。

这里刻意不实现正式网络服务。概念阶段的目标是验证“URL 能否被接受并放入索引”这个想法；只有在项目获得正式立项后，才进入下一个实验的 Protobuf、HTTP、服务协作和完整测试建设。

## 本实验学到什么

- SET 是 100% 的软件工程师，也是测试功能的负责人，不是只执行手工测试的角色。
- 项目早期应优先验证想法。产品尚未稳定时，不应过早投入完整测试基础设施；但要避免把可测试性债务拖到正式开发之后。
- Tech Lead、项目发起人、SWE 和 SET 从不同角度共同维护设计文档。
- SET 通过依赖注入、接口隔离、错误注入和可控超时，让后续小型测试和中型测试变得可行。
- 自动化计划应先覆盖高风险接口和快速反馈路径，不追求一次性自动化所有端到端流程。
- CL 是小而可审查的变更单元；静态检查、测试和评审共同构成绿色提交的门槛。

## 运行

环境要求：JDK 21、Maven 3.9+、Python 3.11+。

```shell
mvn clean verify
java -cp target/classes io.github.xieyongxin.testing.foundations.AddUrlPrototype
python scripts/check_design_document.py
```

原型运行结果应包含 `accepted=true`、标准化 URL 和索引命中结果。文档检查脚本会验证设计文档是否包含目标、接口、风险、测试性和验收标准。

## 文件导航

- [设计文档](DESIGN_DOCUMENT.md)：动态记录项目背景、团队、系统边界和路线图。
- [SET 可测试性评审](TESTABILITY_REVIEW.md)：模拟 SET 对设计提出的具体改进意见。
- [CL 审查记录](CL_REVIEW.md)：记录 SWE 提交、SET 评论、修改和绿色提交条件。
- [开发过程](DEVELOPMENT_PROCESS.md)：按中文提交顺序复盘角色协作。
- [测试计划](TEST_PLAN.md)：定义原型阶段的测试范围和后续扩展边界。
- [书本映射](SOURCE_MAPPING.md)：把章节概念对应到代码、脚本和提交。

