package com.scloud.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "角色保存请求")
public class RoleRequest {
    @Schema(description = "角色名称", example = "系统管理员")
    @NotBlank(message = "角色名称不能为空")
    private String roleName;

    @Schema(description = "角色标识", example = "system_admin")
    @NotBlank(message = "角色标识不能为空")
    private String roleKey;

    @Schema(description = "数据权限范围，1 全部，2 本部门，3 本人", example = "1")
    private Integer dataScope;

    @Schema(description = "状态，1 启用，0 禁用", example = "1")
    private Integer status;
}
