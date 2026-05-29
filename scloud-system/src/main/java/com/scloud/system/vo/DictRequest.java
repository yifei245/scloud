package com.scloud.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "字典保存请求")
public class DictRequest {
    @Schema(description = "字典类型", example = "common_status")
    @NotBlank(message = "字典类型不能为空")
    private String dictType;

    @Schema(description = "字典标签", example = "启用")
    @NotBlank(message = "字典标签不能为空")
    private String dictLabel;

    @Schema(description = "字典值", example = "1")
    @NotBlank(message = "字典值不能为空")
    private String dictValue;

    @Schema(description = "状态，1 启用，0 禁用", example = "1")
    private Integer status;
}
