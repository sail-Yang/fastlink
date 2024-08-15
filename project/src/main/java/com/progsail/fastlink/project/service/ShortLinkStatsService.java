package com.progsail.fastlink.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.progsail.fastlink.project.dto.req.ShortLinkStatsAccessRecordReqDTO;
import com.progsail.fastlink.project.dto.req.ShortLinkStatsReqDTO;
import com.progsail.fastlink.project.dto.resp.ShortLinkStatsAccessRecordRespDTO;
import com.progsail.fastlink.project.dto.resp.ShortLinkStatsRespDTO;

/**
 * @author yangfan
 * @version 1.0
 * @description: 短链接监控信息服务
 * @date 2024/8/12 19:51
 */
public interface ShortLinkStatsService  {
    /**
     * 获取单个短链接监控数据
     *
     * @param requestParam 获取短链接监控数据入参
     * @return 短链接监控数据
     */
    ShortLinkStatsRespDTO oneShortLinkStats(ShortLinkStatsReqDTO requestParam);

    /**
     * 分页获取短链接访问日志记录
     * @param requestParam
     * @return
     */
    public IPage<ShortLinkStatsAccessRecordRespDTO> shortLinkStatsAccessRecord(ShortLinkStatsAccessRecordReqDTO requestParam);
}
