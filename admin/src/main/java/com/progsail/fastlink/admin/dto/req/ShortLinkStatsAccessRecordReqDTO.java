package com.progsail.fastlink.admin.dto.req;

import lombok.Data;

/**
 * @author yangfan
 * @version 1.0
 * @description: 短链接监控访问记录请求参数
 * @date 2024/8/15 21:30
 */

@Data
public class ShortLinkStatsAccessRecordReqDTO{
    /**
     * 完整短链接
     */
    private String fullShortUrl;

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 开始日期
     */
    private String startDate;

    /**
     * 结束日期
     */
    private String endDate;

    /**
     * todo: 远程调用有Bug，这是临时解决方案
     */
    private int current;

    private int size;
}
