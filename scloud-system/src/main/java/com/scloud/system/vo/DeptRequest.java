package com.scloud.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "部门保存请求")
public class DeptRequest {
    @Schema(description = "父部门 ID", example = "0")
    private Long parentId;

    @Schema(description = "部门名称", example = "研发部")
    @NotBlank(message = "部门名称不能为空")
    private String deptName;

    @Schema(description = "状态，1 启用，0 禁用", example = "1")
    private Integer status;
}
