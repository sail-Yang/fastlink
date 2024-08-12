package com.progsail.fastlink.project.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yangfan
 * @version 1.0
 * @description: 操作系统监控响应数据
 * @date 2024/8/12 19:43
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShortLinkStatsOsRespDTO {
        /**
         * 统计
         */
        private Integer cnt;

        /**
         * 操作系统
         */
        private String os;

        /**
         * 占比
         */
        private Double ratio;
}
