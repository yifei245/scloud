package com.scloud.gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class Knife4jResourcesController {

    @GetMapping("/swagger-resources")
    public List<Map<String, String>> swaggerResources() {
        return List.of(
                resource("认证中心", "/v3/api-docs/auth"),
                resource("系统服务", "/v3/api-docs/system"),
                resource("演示服务", "/v3/api-docs/demo"),
                resource("代码生成器", "/v3/api-docs/generator")
        );
    }

    @GetMapping("/swagger-resources/configuration/ui")
    public Map<String, Object> uiConfiguration() {
        return Map.of(
                "deepLinking", true,
                "displayOperationId", false,
                "defaultModelsExpandDepth", 1,
                "defaultModelExpandDepth", 1
        );
    }

    @GetMapping("/swagger-resources/configuration/security")
    public List<Object> securityConfiguration() {
        return List.of();
    }

    private Map<String, String> resource(String name, String url) {
        return Map.of(
                "name", name,
                "url", url,
                "location", url,
                "swaggerVersion", "3.0"
        );
    }
}
