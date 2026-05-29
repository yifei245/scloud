package com.scloud.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "状态修改请求")
public class StatusRequest {
    @Schema(description = "状态，1 启用，0 禁用", example = "1")
    @NotNull(message = "状态不能为空")
    private Integer status;
}
