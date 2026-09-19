# 《Google 软件测试之道》实践

这个仓库用可运行的示例和连续的 Git 提交，实践《Google 软件测试之道》中的工程方法。每个小节都是独立项目，可以单独构建和验证。

## 章节目录

| 章节 | 内容 | 实践项目 |
| --- | --- | --- |
| 2.1.1 | SET 的工作：开发和测试流程 | [迷你订单系统](chapters/ch02/2.1.1-set-work/README.md) |
| 2.1.2–2.1.8 | SET 角色、项目早期、设计文档与可测试性 | [SET 基础职责实验](chapters/ch02/2.1.2-2.1.8-set-foundations/README.md) |
| 2.1.9 | SET 工作流程实例：AddUrl | [AddUrl 完整工作流](chapters/ch02/2.1.9-addurl-workflow/README.md) |
| 2.1.10–2.1.14 | 测试执行、测试规模与运行要求 | [测试执行平台](chapters/ch02/2.1.10-2.1.14-test-platform/README.md) |

## 阅读方式

1. 阅读对应目录的 `README.md`，理解章节要点与 Demo 的映射。
2. 阅读 `DEVELOPMENT_PROCESS.md`，了解 SWE、SET 和构建工程角色的协作过程。
3. 使用 `git log --reverse --oneline` 查看中文提交历史。
4. 使用 `git show <提交>` 观察每个阶段引入的代码和测试。

## 后续章节的统一实践规则

- 每个实验保留 `README.md`、`DEVELOPMENT_PROCESS.md`、`TEST_PLAN.md` 和 `SOURCE_MAPPING.md`。
- 文档、角色记录、评审意见和 Git 提交信息使用中文；代码标识符使用英文。
- SWE 编写产品代码和基础小型测试，SET 参与设计评审、可测试性改进、mock/fake 和中大型测试。
- 每个实验按小提交推进，完成本批次的本地验证后再推送 `main`。
