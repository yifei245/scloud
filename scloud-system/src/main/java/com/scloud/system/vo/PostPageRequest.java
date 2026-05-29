package com.scloud.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "岗位分页查询")
public class PostPageRequest extends BasePageRequest {
    @Schema(description = "岗位编码，支持模糊查询", example = "dev")
    private String postCode;

    @Schema(description = "岗位名称，支持模糊查询", example = "开发工程师")
    private String postName;

    @Schema(description = "状态，1 启用，0 禁用", example = "1")
    private Integer status;
}
