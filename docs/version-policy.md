# 版本策略

本项目优先使用新的稳定技术版本，但必须保持同一兼容线内一致。

## 基线

- JDK: 21
- Spring Boot: 3.5.x 稳定版
- Spring Cloud: 2025.0.x 稳定版
- Spring Cloud Alibaba: 适配 Spring Boot 3.5.x / Spring Cloud 2025.0.x 的稳定版

## 当前组合

- Spring Boot: 3.5.0
- Spring Cloud: 2025.0.2
- Spring Cloud Alibaba: 2025.0.0.0
- Knife4j Gateway: 5.0.2
- Springdoc OpenAPI: 2.8.13
- Nacos Server: 3.0.3

## 规则

1. 不使用快照版、里程碑版、RC 版。
2. 不主动升级到 Spring Boot 4.x 或 Spring Cloud 2025.1.x，除非整体兼容性评估后再统一升级。
3. Spring Boot 3.5.x 内的小版本可以升级，但升级前必须确认 Spring Cloud 2025.0.x 和 Spring Cloud Alibaba 2025.0.x 兼容。
4. Spring Cloud 使用 2025.0.x 的最新稳定服务版本。
5. Spring Cloud Alibaba 使用官方声明适配 Spring Boot 3.5.x / Spring Cloud 2025.0.x 的稳定版本。
6. 子依赖优先由 Spring Boot、Spring Cloud、Spring Cloud Alibaba BOM 管理；只有修复兼容问题时才显式覆盖版本，并在 README 或本文件说明原因。
7. Gateway 保持轻量，不引入业务服务公共模块中的 WebMVC、MyBatis、DataSource 相关依赖。
8. Redis/Redisson 依赖需要匹配 Spring Boot 3.5 使用的 Spring Data 3.5 兼容线。
9. 业务服务只暴露 Springdoc OpenAPI JSON，Knife4j 只放在 Gateway 做统一文档入口；Springdoc 固定在 2.8.x 最新稳定线，避免业务服务混入 Knife4j MVC UI/Customizer 后的兼容问题。
