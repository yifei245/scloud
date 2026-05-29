package com.scloud.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "岗位保存请求")
public class PostRequest {
    @Schema(description = "岗位编码", example = "dev")
    @NotBlank(message = "岗位编码不能为空")
    private String postCode;

    @Schema(description = "岗位名称", example = "开发工程师")
    @NotBlank(message = "岗位名称不能为空")
    private String postName;

    @Schema(description = "状态，1 启用，0 禁用", example = "1")
    private Integer status;
}
