package com.scloud.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class BasePageRequest {
    @Schema(description = "页码，从 1 开始", example = "1")
    private Long pageNo = 1L;

    @Schema(description = "每页条数，最大 100", example = "10")
    private Long pageSize = 10L;

    public Long getPageNo() {
        return pageNo == null || pageNo < 1 ? 1L : pageNo;
    }

    public Long getPageSize() {
        if (pageSize == null || pageSize < 1) {
            return 10L;
        }
        return Math.min(pageSize, 100L);
    }
}
