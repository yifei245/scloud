package com.scloud.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "部门分页查询")
public class DeptPageRequest extends BasePageRequest {
    @Schema(description = "父部门 ID", example = "0")
    private Long parentId;

    @Schema(description = "部门名称，支持模糊查询", example = "总部")
    private String deptName;

    @Schema(description = "状态，1 启用，0 禁用", example = "1")
    private Integer status;
}
