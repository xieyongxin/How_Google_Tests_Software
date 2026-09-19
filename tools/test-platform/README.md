# 教学测试执行平台

`run_tests.py` 是对书中测试执行器、TAP/Unit Test Dashboard 和提交队列思想的本地化模拟。它不替代 Maven/JUnit，只负责从 `test-manifest.json` 读取测试目标、按规模调度命令、记录耗时并生成报告。

```shell
python tools/test-platform/run_tests.py --size small
python tools/test-platform/run_tests.py --all
python tools/test-platform/run_tests.py --affected --base origin/main
python tools/test-platform/run_tests.py --all --random-order --isolation
```

报告默认写入 `target/test-platform-reports/`：

- `test-results.json`：机器可读的目标、退出码和耗时。
- `summary.md`：中文汇总表。
- `junit.xml`：供 CI 上传或测试报告插件读取。
- `*.log`：每个目标的 Maven 标准输出和错误输出。

测试规模是教学约定：`small` 不使用网络和持久化；`medium` 组合多个模块和内存 fake；`large` 验证完整 HTTP 或文件持久化。受影响测试无法安全判断时会降级为全量运行。
