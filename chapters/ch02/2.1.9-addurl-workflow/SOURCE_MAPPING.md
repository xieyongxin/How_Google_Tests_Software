# 章节概念映射

| 书中概念 | 本实验实现 |
| --- | --- |
| Protocol Buffer 是第一份源代码 | `src/main/proto/addurl.proto` 和 protobuf Maven 插件 |
| SET 先实现接口和 fake | `AddUrlBackendClient`、`FakeAddUrlBackendClient`、`DelayedAddUrlBackendClient` |
| AddUrlFrontend 解析 HTTP | `AddUrlFrontend` |
| AddUrlService 校验并写后端 | `DefaultAddUrlService` |
| Bigtable/GFS 等持久层 | `IndexStore`、`InMemoryIndexStore`、`FileIndexStore` |
| 小型测试 | `AddUrlFrontendTest`、`ProtocolCompatibilityTest` |
| 中型测试 | `AddUrlWorkflowIT` |
| 大型测试 | `AddUrlHttpIT`、`FileIndexStoreIT` |
| CL 中功能和测试一起提交 | `DEVELOPMENT_PROCESS.md` 中的阶段提交 |

