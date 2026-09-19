# 书本概念到实现映射

| 书中概念 | 教学实现 |
| --- | --- |
| 测试规模和测试金字塔 | JUnit `@Tag("small"/"medium"/"large")` 与 `test-manifest.json` |
| TAP/测试执行器 | `tools/test-platform/run_tests.py` |
| Unit Test Dashboard | `test-results.json`、`summary.md`、`junit.xml` |
| 受影响测试 | `--affected` 的 git diff 路径映射 |
| 测试隔离 | 固定资源扫描、随机端口、JUnit `@TempDir`、临时环境变量 |
| 提交队列 | PR 工作流的受影响测试与 main 工作流的全量测试 |
| 持续集成 | `.github/workflows/pull-request-tests.yml`、`main-tests.yml` |

这些文件是本地教学替代物，明确不等同于 Google 内部系统。
