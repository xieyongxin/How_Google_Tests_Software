# AddUrl 测试计划

## 测试规模

本实验使用 JUnit 5 标签表达书中的测试大小：

- `small`：不启动 HTTP、不访问网络、不写文件；使用 fake 或 mock 隔离依赖。
- `medium`：组合 Frontend、Service 和内存 IndexStore，验证模块交互。
- `large`：启动 JDK 内置 HTTP 服务或使用临时文件索引，验证完整应用行为。

## 测试矩阵

| 测试类 | 规模 | 场景 |
| --- | --- | --- |
| `AddUrlFrontendTest` | small | URL 解析、comment 解码、缺少 URL、错误映射、超时 |
| `ProtocolCompatibilityTest` | small | 字段编号和旧消息读取 |
| `AddUrlWorkflowIT` | medium | Frontend、Service、内存索引成功与错误协作 |
| `AddUrlHttpIT` | large | 完整 HTTP 请求链路 |
| `FileIndexStoreIT` | large | 文件索引写入、关闭和重新读取 |

当前实际规模：14 个 small、2 个 medium、3 个 large（集成测试由 Failsafe 执行）。

## 独立性要求

- 每个测试使用自己的服务实例、随机端口或临时目录。
- 测试结束后关闭 HTTP server，并删除临时目录。
- 不依赖测试执行顺序，不依赖公网或共享文件。

验收命令：

```shell
mvn clean verify
java -jar target/addurl-workflow.jar
```
