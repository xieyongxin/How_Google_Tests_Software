# 2.1.10–2.1.14 测试执行、规模与持续集成

本实验把书中测试执行器、测试规模、隔离性、受影响测试和持续集成思想做成一个可在本地运行的教学实现。它不声称复制 Google 内部 Mondrian、TAP 或 Unit Test Dashboard，而是用 `test-manifest.json`、Python 标准库执行器和 GitHub Actions 展示相同的工程原则。

## 快速运行

在仓库根目录执行：

```shell
python tools/test-platform/run_tests.py --size small
python tools/test-platform/run_tests.py --all
python tools/test-platform/run_tests.py --affected --base origin/main
python tools/test-platform/run_tests.py --all --random-order --seed 20260919 --isolation
```

报告在 `target/test-platform-reports/`，包括 JSON、Markdown、JUnit XML 和每个目标的日志。

## 测试规模

| 规模 | 教学边界 | 当前目标数 |
| --- | --- | ---: |
| small | 单模块、无网络、无持久化外部依赖 | 14 |
| medium | 多模块协作，可使用内存 fake | 4 |
| large | HTTP 链路或临时文件持久化 | 2 |

当前清单是 14/4/2，用于练习调度和速度分析，不代表所有 Google 项目的固定比例。

执行器实跑结果：small 14/14、medium 4/4、large 2/2 全部通过；另有 4 项 Python 执行器自测通过。

## 提交队列和 CI 对应

- PR 工作流先计算变更范围，只运行受影响目标；无法判断时安全降级为全量。
- `main` 和定时工作流运行全量、随机顺序和隔离检查。
- 报告作为构建产物上传；仓库不直接修改分支保护设置，管理员可将 PR 工作流设为必需检查。
- 固定端口、固定目录和持久化状态是隔离风险；代码使用随机端口、JUnit 临时目录和执行器临时目录。

建议仓库管理员在 GitHub 分支保护中将 `PR 受影响测试 / affected-tests` 设为必需状态检查；这一步需要仓库权限，因此只提供配置说明，不由 Demo 自动修改。
