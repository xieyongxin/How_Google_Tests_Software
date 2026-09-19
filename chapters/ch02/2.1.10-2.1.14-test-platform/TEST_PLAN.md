# 测试计划

## 范围

1. 执行器能按 `small`、`medium`、`large` 选择目标。
2. `--all` 运行全部目标并生成三种报告。
3. `--affected` 根据 git diff 选择订单、AddUrl 或基础实验的相关目标。
4. 修改公共契约、父 POM、清单或执行器时安全降级为全量。
5. `--random-order` 支持固定种子，便于复现顺序相关问题。
6. `--isolation` 检查固定端口/目录，并为子进程提供临时目录。

## 验收命令

```shell
python -m unittest discover -s tools/test-platform -p 'test_*.py'
python tools/test-platform/run_tests.py --size small --isolation
python tools/test-platform/run_tests.py --size medium --isolation
python tools/test-platform/run_tests.py --size large --isolation
python tools/test-platform/run_tests.py --all --random-order --seed 20260919 --isolation
```

## 风险和边界

- 执行器是教学实现，命令仍由 Maven/JUnit 执行；它不替代生产级分布式调度。
- 受影响分析使用路径规则，遇到未知路径必须全量运行，避免漏测。
- 隔离扫描是静态启发式检查；动态资源泄漏仍由测试和 CI 环境共同发现。
