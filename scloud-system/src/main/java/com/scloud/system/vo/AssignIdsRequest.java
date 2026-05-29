package com.scloud.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;

@Data
@Schema(description = "ID 分配请求")
public class AssignIdsRequest {
    @Schema(description = "ID 列表", example = "[1,2]")
    private List<Long> ids;
}
