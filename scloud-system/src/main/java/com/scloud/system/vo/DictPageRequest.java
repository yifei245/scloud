package com.scloud.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "字典分页查询")
public class DictPageRequest extends BasePageRequest {
    @Schema(description = "字典类型，精确查询", example = "common_status")
    private String dictType;

    @Schema(description = "字典标签，支持模糊查询", example = "启用")
    private String dictLabel;

    @Schema(description = "字典值，支持模糊查询", example = "1")
    private String dictValue;

    @Schema(description = "状态，1 启用，0 禁用", example = "1")
    private Integer status;
}
