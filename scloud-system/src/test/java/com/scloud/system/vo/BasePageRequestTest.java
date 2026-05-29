package com.scloud.system.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class BasePageRequestTest {
    @Test
    void shouldNormalizeInvalidPageParams() {
        BasePageRequest request = new BasePageRequest();
        request.setPageNo(0L);
        request.setPageSize(1000L);

        assertThat(request.getPageNo()).isEqualTo(1L);
        assertThat(request.getPageSize()).isEqualTo(100L);
    }
}
