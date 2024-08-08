package com.progsail.fastlink.project.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.progsail.fastlink.project.dao.entity.ShortLinkOsStatsDO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

/**
 * @author yangfan
 * @version 1.0
 * @description: TODO
 * @date 2024/8/8 15:49
 */
public interface ShortLinkOsStatsMapper extends BaseMapper<ShortLinkOsStatsDO> {
    /**
     * 操作系统访问监控数据
     */
    @Insert("INSERT INTO t_link_os_stats (full_short_url, gid, date, cnt, os, create_time, update_time, del_flag) " +
            "VALUES( #{linkOsStats.fullShortUrl}, #{linkOsStats.gid}, #{linkOsStats.date}, #{linkOsStats.cnt}, #{linkOsStats.os}, NOW(), NOW(), 0) " +
            "ON DUPLICATE KEY UPDATE cnt = cnt +  #{linkOsStats.cnt};")
    void shortLinkOsState(@Param("linkOsStats") ShortLinkOsStatsDO linkOsStatsDO);
}
