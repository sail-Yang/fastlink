package com.progsail.fastlink.project.dto.req;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.progsail.fastlink.project.dao.entity.ShortLinkAccessLogDO;
import lombok.Data;

/**
 * @author yangfan
 * @version 1.0
 * @description: 短链接监控访问记录请求参数
 * @date 2024/8/15 21:30
 */

@Data
public class ShortLinkStatsAccessRecordReqDTO extends Page<ShortLinkAccessLogDO> {
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
}
