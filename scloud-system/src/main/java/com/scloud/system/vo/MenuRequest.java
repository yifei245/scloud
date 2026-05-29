package com.scloud.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "菜单保存请求")
public class MenuRequest {
    @Schema(description = "父菜单 ID", example = "0")
    private Long parentId;

    @Schema(description = "菜单名称", example = "用户管理")
    @NotBlank(message = "菜单名称不能为空")
    private String menuName;

    @Schema(description = "权限标识", example = "system:user:query")
    private String permission;

    @Schema(description = "类型，1 菜单，2 按钮", example = "1")
    private Integer type;

    @Schema(description = "状态，1 启用，0 禁用", example = "1")
    private Integer status;
}
