package com.scloud.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "角色分页查询")
public class RolePageRequest extends BasePageRequest {
    @Schema(description = "角色名称，支持模糊查询", example = "管理员")
    private String roleName;

    @Schema(description = "角色标识，支持模糊查询", example = "admin")
    private String roleKey;

    @Schema(description = "数据权限范围", example = "1")
    private Integer dataScope;

    @Schema(description = "状态，1 启用，0 禁用", example = "1")
    private Integer status;
}
