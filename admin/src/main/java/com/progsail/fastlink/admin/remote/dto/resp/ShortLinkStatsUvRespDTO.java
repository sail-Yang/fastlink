package com.progsail.fastlink.admin.remote.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yangfan
 * @version 1.0
 * @description: uv监控响应数据
 * @date 2024/8/12 19:43
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShortLinkStatsUvRespDTO {
        /**
         * 统计
         */
        private Integer cnt;

        /**
         * 访客类型
         */
        private String uvType;

        /**
         * 占比
         */
        private Double ratio;
}
