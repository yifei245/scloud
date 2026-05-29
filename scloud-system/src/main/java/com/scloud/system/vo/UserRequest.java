package com.scloud.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "用户保存请求")
public class UserRequest {
    @Schema(description = "用户名", example = "zhangsan")
    @NotBlank(message = "用户名不能为空")
    private String username;

    @Schema(description = "密码；新增必填，修改为空则不变", example = "123456")
    private String password;

    @Schema(description = "昵称", example = "张三")
    private String nickname;

    @Schema(description = "手机号", example = "18800000001")
    private String mobile;

    @Schema(description = "邮箱", example = "zhangsan@scloud.local")
    private String email;

    @Schema(description = "状态，1 启用，0 禁用", example = "1")
    private Integer status;

    @Schema(description = "部门 ID", example = "1")
    private Long deptId;
}
