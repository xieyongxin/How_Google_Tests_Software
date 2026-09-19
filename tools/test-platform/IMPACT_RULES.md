# 受影响测试选择规则

执行器根据 `git diff` 的仓库相对路径和清单中的 `inputs` 做保守映射：

| 变更位置 | 选择范围 |
| --- | --- |
| `2.1.1-set-work/order-service/` | 订单 small 与 order-app medium |
| `2.1.1-set-work/fake-services/` | fake small 与 order-app medium |
| `2.1.1-set-work/order-app/` | order-app medium |
| `2.1.2-2.1.8-set-foundations/` | 原型 small |
| AddUrl `frontend/` | 前端 small、AddUrl medium/large |
| AddUrl `service/` 或 `proto/` | 服务/协议 small、AddUrl medium/large |
| 执行器、清单、父 POM、公共契约 | 全量测试 |
| 无法识别的路径 | 全量测试 |

这是教学实现中的安全默认值：宁可多跑，也不因错误的依赖判断漏掉回归测试。
