# 2.1.9 AddUrl 完整工作流程

本实验把前一个概念原型推进为书中描述的正式 AddUrl 服务：客户端提交 URL，前端解析 HTTP 请求，后端服务校验并写入索引存储。

```text
HTTP Client
    ↓
AddUrlFrontend
    ↓（可替换的后端客户端）
AddUrlService
    ↓
IndexStore
```

## 本实验学到什么

- Protocol Buffer 是接口和协议的第一份源代码；Java 类型由 Maven 自动生成。
- SET 先审查协议，再提供 fake、延迟依赖和可注入的测试边界，让集成测试可以早于真实服务完成。
- SWE 编写服务和基础小型测试；SET 补充错误、超时、兼容性和跨模块测试。
- 小型测试隔离 HTTP、网络和文件系统；中型测试验证模块协作；大型测试验证完整 HTTP 链路和持久化行为。

## 构建与运行

环境要求：JDK 21、Maven 3.9+。

```shell
mvn clean verify
java -jar target/addurl-workflow.jar
```

默认命令会启动一个临时本地 HTTP 服务，提交一次 URL，打印响应后退出。启动长期运行服务：

```shell
java -jar target/addurl-workflow.jar --serve 8080
```

提交请求示例：

```shell
curl "http://localhost:8080/addurl?url=https%3A%2F%2Fwww.example.com&comment=Demo+comment"
```

## 文件导航

- `src/main/proto/addurl.proto`：协议定义和字段编号。
- `src/main/java/.../protocol`：Maven 生成的 Java 消息类型（不手写、不提交）。
- `src/main/java/.../service`：服务接口、校验、索引存储和前端 HTTP 适配器。
- `src/test/java/.../AddUrlFrontendTest.java`：小型前端测试。
- `src/test/java/.../*IT.java`：中型、大型工作流测试。
- `src/main/java/.../app`：JDK 内置 HTTP Server 和命令行入口。
- [开发过程](DEVELOPMENT_PROCESS.md)：按 SWE/SET 角色记录中文提交。
- [测试计划](TEST_PLAN.md)：记录 Small/Medium/Large 测试边界。
- [书本映射](SOURCE_MAPPING.md)：把章节概念映射到代码和提交。

本实验当前验证结果：14 个小型测试、2 个中型测试和 3 个大型测试全部通过；`java -jar target/addurl-workflow.jar` 会启动随机端口服务、提交一次 URL 并输出 `HTTP 200`。测试数量用于演示调度，不代表 Google 项目的固定比例。
