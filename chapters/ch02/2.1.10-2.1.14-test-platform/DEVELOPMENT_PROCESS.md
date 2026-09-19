# 开发过程：从测试规模到提交队列

## 角色分工

- **SWE**：给产品测试标注规模，并保持测试本身可重复运行。
- **SET**：设计清单、执行器、受影响测试选择、随机顺序和隔离检查。
- **构建工程角色**：维护 Python/JDK 版本、报告上传和 PR/main 工作流。

## 阶段记录

| 阶段 | 中文提交 | 交付物 | 验证 |
| --- | --- | --- | --- |
| 1 | `8b11984 工程：定义测试规模标签和测试目标清单` | JUnit 标签、`test-manifest.json` | 三个 Maven 工程测试通过 |
| 2 | `c694143 工程：实现统一测试执行器与报告` | `run_tests.py`、JSON/Markdown/JUnit 报告 | small/medium/large 实跑 |
| 3 | `e335418 测试：验证测试隔离、随机顺序和临时资源` | 执行器自测、随机顺序和隔离规则 | Python unittest 4 项通过 |
| 4 | `55df462 工程：实现依赖分析与受影响测试选择` | `git diff` 映射和安全降级 | affected dry-run |
| 5 | `cd8edeb 持续集成：配置PR快速验证和主干全量验证` | 两个 GitHub Actions 工作流 | 工作流命令与本地执行器一致 |
| 6 | `4750acd 文档：总结测试规模、速度与提交队列实践` | 本章四份文档和根目录路线图 | 全量验收 |
| 7 | `df1aa69`、`21e6b85` | 默认全量入口、空变更安全降级和执行器自测修复 | 6 项 Python 自测 |
| 8 | `55ccb6f 工程：接入JaCoCo覆盖率报告并完善CI产物` | JaCoCo HTML 报告和 CI artifact | 三个 Maven 工程生成报告 |

每个阶段都保留独立提交，学习者可用 `git log --reverse` 和 `git show` 观察测试平台从清单到 CI 的增长过程。

## 实际运行记录

- `python -m unittest discover -s tools/test-platform -p 'test_*.py'`：6 项通过。
- small：14/14 通过，耗时约 233 秒。
- medium：4/4 通过，耗时约 237 秒。
- large：2/2 通过，耗时约 43 秒。
- 随机顺序使用 `--seed 20260919`，隔离检查未发现固定端口或固定目录引用。
