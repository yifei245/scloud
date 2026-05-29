package com.scloud.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "用户分页查询")
public class UserPageRequest extends BasePageRequest {
    @Schema(description = "用户名，支持模糊查询", example = "admin")
    private String username;

    @Schema(description = "昵称，支持模糊查询", example = "管理员")
    private String nickname;

    @Schema(description = "手机号，支持模糊查询", example = "188")
    private String mobile;

    @Schema(description = "邮箱，支持模糊查询", example = "admin@scloud.local")
    private String email;

    @Schema(description = "部门 ID", example = "1")
    private Long deptId;

    @Schema(description = "状态，1 启用，0 禁用", example = "1")
    private Integer status;
}
