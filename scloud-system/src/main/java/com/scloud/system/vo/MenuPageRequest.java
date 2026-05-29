package com.scloud.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "菜单分页查询")
public class MenuPageRequest extends BasePageRequest {
    @Schema(description = "父菜单 ID", example = "0")
    private Long parentId;

    @Schema(description = "菜单名称，支持模糊查询", example = "系统管理")
    private String menuName;

    @Schema(description = "权限标识，支持模糊查询", example = "system:user")
    private String permission;

    @Schema(description = "类型，1 菜单，2 按钮", example = "1")
    private Integer type;

    @Schema(description = "状态，1 启用，0 禁用", example = "1")
    private Integer status;
}
