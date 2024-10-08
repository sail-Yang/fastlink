package com.progsail.fastlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.progsail.fastlink.project.common.convention.exception.ServiceException;
import com.progsail.fastlink.project.dao.entity.*;
import com.progsail.fastlink.project.dao.mapper.*;
import com.progsail.fastlink.project.dto.req.ShortLinkStatsAccessRecordReqDTO;
import com.progsail.fastlink.project.dto.req.ShortLinkStatsReqDTO;
import com.progsail.fastlink.project.dto.resp.*;
import com.progsail.fastlink.project.service.ShortLinkStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yangfan
 * @version 1.0
 * @description: 短链接监控信息服务实现
 * @date 2024/8/12 19:52
 */
@Service
@RequiredArgsConstructor
public class ShortLinkStatsServiceImpl implements ShortLinkStatsService {
    private final ShortLinkAccessStatsMapper shortLinkAccessStatsMapper;
    private final ShortLinkLocaleStatsMapper shortLinkLocaleStatsMapper;
    private final ShortLinkAccessLogMapper shortLinkAccessLogMapper;
    private final ShortLinkBrowserStatsMapper shortLinkBrowserStatsMapper;
    private final ShortLinkOsStatsMapper shortLinkOsStatsMapper;
    private final ShortLinkDeviceStatsMapper shortLinkDeviceStatsMapper;
    private final ShortLinkNetworkStatsMapper shortLinkNetworkStatsMapper;

    @Override
    public ShortLinkStatsRespDTO oneShortLinkStats(ShortLinkStatsReqDTO requestParam) {
        /*
            基础访问详情
         */
        List<ShortLinkAccessStatsDO> listedDailyStats = shortLinkAccessStatsMapper.listStatsBySingleShortLink(requestParam);
        if (CollUtil.isEmpty(listedDailyStats)) {
            return null;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        List<ShortLinkStatsAccessDailyRespDTO> dailyStats = new ArrayList<>();
        List<String> rangeDates = DateUtil.rangeToList(DateUtil.parse(requestParam.getStartDate()), DateUtil.parse(requestParam.getEndDate()), DateField.DAY_OF_MONTH).stream()
                .map(DateUtil::formatDate)
                .toList();
        rangeDates.forEach(each -> listedDailyStats.stream()
                .filter(item -> Objects.equals(each, DateUtil.formatDate(item.getDate())))
                .findFirst()
                .ifPresentOrElse(item -> {
                    ShortLinkStatsAccessDailyRespDTO accessDailyRespDTO = null;
                    try {
                        accessDailyRespDTO = ShortLinkStatsAccessDailyRespDTO.builder()
                                .date(dateFormat.parse(each))
                                .pv(item.getPv())
                                .uv(item.getUv())
                                .uip(item.getUip())
                                .build();
                        dailyStats.add(accessDailyRespDTO);
                    } catch (ParseException e) {
                        throw new ServiceException("时间解析错误");
                    }
                }, () -> {
                    ShortLinkStatsAccessDailyRespDTO accessDailyRespDTO = null;
                    try {
                        accessDailyRespDTO = ShortLinkStatsAccessDailyRespDTO.builder()
                                .date(dateFormat.parse(each))
                                .pv(0)
                                .uv(0)
                                .uip(0)
                                .build();
                        dailyStats.add(accessDailyRespDTO);
                    } catch (ParseException e) {
                        throw new ServiceException("时间解析错误");
                    }
                }));
        /*
            国内地区访问详情
         */
        List<ShortLinkLocaleStatsDO> listedLocaleStats = shortLinkLocaleStatsMapper.listLocaleBySingleShortLink(requestParam);
        List<ShortLinkStatsLocaleCNRespDTO> localeCNStats = new ArrayList<>();
        // 计算国内地区访问总数
        int localeCNSum = listedLocaleStats.stream()
                .mapToInt(ShortLinkLocaleStatsDO::getCnt)
                .sum();
        listedLocaleStats.forEach(each -> {
            double ratio = (double) each.getCnt() / localeCNSum;
            // 保留两位小数
            double actualRatio = Math.round(ratio * 100.0) / 100.0;
            ShortLinkStatsLocaleCNRespDTO localeCNRespDTO = ShortLinkStatsLocaleCNRespDTO.builder()
                    .cnt(each.getCnt())
                    .locale(each.getProvince())
                    .ratio(actualRatio)
                    .build();
            localeCNStats.add(localeCNRespDTO);
        });
        /*
            小时访问详情
         */
        List<Integer> hourStats = new ArrayList<>();
        List<ShortLinkAccessStatsDO> listedHourStats = shortLinkAccessStatsMapper.listHourStatsBySingleShortLink(requestParam);
        for(int i=0;i<24;i++) {
            AtomicInteger hour = new AtomicInteger(i);
            //统计各小时访问次数，如果没有则为0,有的话就直接取pv值
            int hourCnt = listedHourStats.stream()
                    .filter(each -> Objects.equals(each.getHour(), hour.get()))
                    .findFirst()
                    .map(ShortLinkAccessStatsDO::getPv)
                    .orElse(0);
            hourStats.add(hourCnt);
        }
        /*
            高频访问IP详情
         */
        List<ShortLinkStatsTopIpRespDTO> topIpStats = new ArrayList<>();
        List<HashMap<String,Object>> listedTopIpStats = shortLinkAccessLogMapper.listTopIpBySingleShortLink(requestParam);
        listedTopIpStats.forEach(each -> {
            ShortLinkStatsTopIpRespDTO shortLinkStatsTopIpRespDTO = ShortLinkStatsTopIpRespDTO.builder()
                    .ip(each.get("ip").toString())
                    .cnt(Integer.parseInt(each.get("count").toString()))
                    .build();
            topIpStats.add(shortLinkStatsTopIpRespDTO);
        });
        /*
            一周访问详情
         */
        List<Integer> weekdayStats = new ArrayList<>();
        List<ShortLinkAccessStatsDO> listedWeekDayStats = shortLinkAccessStatsMapper.listWeekdayStatsBySingleShortLink(requestParam);
        for(int i=1;i<=7;i++) {
            AtomicInteger weekday = new AtomicInteger(i);
            //统计各星期访问次数，如果没有则为0,有的话就直接取pv值
            int weekdayCnt = listedWeekDayStats.stream()
                    .filter(each -> Objects.equals(each.getWeekday(), weekday.get()))
                    .findFirst()
                    .map(ShortLinkAccessStatsDO::getPv)
                    .orElse(0);
            weekdayStats.add(weekdayCnt);
        }
        /*
            浏览器访问详情
         */
        List<ShortLinkStatsBrowserRespDTO> browserStats = new ArrayList<>();
        List<HashMap<String, Object>> listedBrowserStats = shortLinkBrowserStatsMapper.listBrowserStatsBySingleShortLink(requestParam);
        int browserSum = listedBrowserStats.stream()
                .mapToInt(each -> Integer.parseInt(each.get("count").toString()))
                .sum();
        listedBrowserStats.forEach(each -> {
            double ratio = (double) Integer.parseInt(each.get("count").toString()) / browserSum;
            double actualRatio = Math.round(ratio * 100.0) / 100.0;
            ShortLinkStatsBrowserRespDTO browserRespDTO = ShortLinkStatsBrowserRespDTO.builder()
                    .cnt(Integer.parseInt(each.get("count").toString()))
                    .browser(each.get("browser").toString())
                    .ratio(actualRatio)
                    .build();
            browserStats.add(browserRespDTO);
        });
        /*
            操作系统访问详情
         */
        List<ShortLinkStatsOsRespDTO> osStats = new ArrayList<>();
        List<HashMap<String, Object>> listedOsStats = shortLinkOsStatsMapper.listOsStatsBySingleShortLink(requestParam);
        int osSum = listedOsStats.stream()
                .mapToInt(each -> Integer.parseInt(each.get("count").toString()))
                .sum();
        listedOsStats.forEach(each -> {
            double ratio = (double) Integer.parseInt(each.get("count").toString()) / osSum;
            double actualRatio = Math.round(ratio * 100.0) / 100.0;
            ShortLinkStatsOsRespDTO osRespDTO = ShortLinkStatsOsRespDTO.builder()
                    .cnt(Integer.parseInt(each.get("count").toString()))
                    .os(each.get("os").toString())
                    .ratio(actualRatio)
                    .build();
            osStats.add(osRespDTO);
        });
        /*
            访问设备类型详情
         */
        List<ShortLinkStatsDeviceRespDTO> deviceStats = new ArrayList<>();
        List<ShortLinkDeviceStatsDO> listedDeviceStats = shortLinkDeviceStatsMapper.listDeviceStatsBySingleShortLink(requestParam);
        int deviceSum = listedDeviceStats.stream()
                .mapToInt(ShortLinkDeviceStatsDO::getCnt)
                .sum();
        listedDeviceStats.forEach(each -> {
            double ratio = (double) each.getCnt() / deviceSum;
            double actualRatio = Math.round(ratio * 100.0) / 100.0;
            ShortLinkStatsDeviceRespDTO deviceRespDTO = ShortLinkStatsDeviceRespDTO.builder()
                    .cnt(each.getCnt())
                    .device(each.getDevice())
                    .ratio(actualRatio)
                    .build();
            deviceStats.add(deviceRespDTO);
        });
        /*
            访问网络类型详情
         */
        List<ShortLinkStatsNetworkRespDTO> networkStats = new ArrayList<>();
        List<ShortLinkNetworkStatsDO> listedNetworkStats = shortLinkNetworkStatsMapper.listNetworkStatsBySingleShortLink(requestParam);
        int networkSum = listedNetworkStats.stream()
                .mapToInt(ShortLinkNetworkStatsDO::getCnt)
                .sum();
        listedNetworkStats.forEach(each -> {
            double ratio = (double) each.getCnt() / networkSum;
            double actualRatio = Math.round(ratio * 100.0) / 100.0;
            ShortLinkStatsNetworkRespDTO networkRespDTO = ShortLinkStatsNetworkRespDTO.builder()
                    .cnt(each.getCnt())
                    .network(each.getNetwork())
                    .ratio(actualRatio)
                    .build();
            networkStats.add(networkRespDTO);
        });
        /*
            访客访问类型详情
         */
        List<ShortLinkStatsUvRespDTO> uvTypeStats = new ArrayList<>();
        HashMap<String, Object> findUvTypeStats = shortLinkAccessLogMapper.findUvTypeCntBySingleShortLink(requestParam);
        int oldUserCnt = Integer.parseInt(findUvTypeStats.get("oldUserCnt").toString());
        int newUserCnt = Integer.parseInt(findUvTypeStats.get("newUserCnt").toString());
        int uvSum = oldUserCnt + newUserCnt;
        double oldRatio = (double) oldUserCnt / uvSum;
        double actualOldRatio = Math.round(oldRatio * 100.0) / 100.0;
        double newRatio = (double) newUserCnt / uvSum;
        double actualNewRatio = Math.round(newRatio * 100.0) / 100.0;
        ShortLinkStatsUvRespDTO newUvRespDTO = ShortLinkStatsUvRespDTO.builder()
                .uvType("newUser")
                .cnt(newUserCnt)
                .ratio(actualNewRatio)
                .build();
        uvTypeStats.add(newUvRespDTO);
        ShortLinkStatsUvRespDTO oldUvRespDTO = ShortLinkStatsUvRespDTO.builder()
                .uvType("oldUser")
                .cnt(oldUserCnt)
                .ratio(actualOldRatio)
                .build();
        uvTypeStats.add(oldUvRespDTO);
        return ShortLinkStatsRespDTO.builder()
                .daily(BeanUtil.copyToList(dailyStats, ShortLinkStatsAccessDailyRespDTO.class))
                .localeCnStats(localeCNStats)
                .hourStats(hourStats)
                .topIpStats(topIpStats)
                .weekdayStats(weekdayStats)
                .browserStats(browserStats)
                .osStats(osStats)
                .deviceStats(deviceStats)
                .networkStats(networkStats)
                .uvTypeStats(uvTypeStats)
                .build();
    }

    @Override
    public IPage<ShortLinkStatsAccessRecordRespDTO> shortLinkStatsAccessRecord(ShortLinkStatsAccessRecordReqDTO requestParam) {
        LambdaQueryWrapper<ShortLinkAccessLogDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkAccessLogDO.class)
                .eq(ShortLinkAccessLogDO::getGid, requestParam.getGid())
                .eq(ShortLinkAccessLogDO::getFullShortUrl, requestParam.getFullShortUrl())
                .between(ShortLinkAccessLogDO::getCreateTime, requestParam.getStartDate(), requestParam.getEndDate())
                .eq(ShortLinkAccessLogDO::getDelFlag, 0)
                .orderByDesc(ShortLinkAccessLogDO::getCreateTime);
        //分页查询log表所有记录
        IPage<ShortLinkAccessLogDO> shortLinkAccessLogsDOIPage = shortLinkAccessLogMapper.selectPage(requestParam, queryWrapper);
        //转成返回实体
        IPage<ShortLinkStatsAccessRecordRespDTO> actualResult = shortLinkAccessLogsDOIPage.convert(each -> BeanUtil.toBean(each, ShortLinkStatsAccessRecordRespDTO.class));
        //提取User列表
        List<String> userAccessLogsList = actualResult.getRecords().stream()
                .map(ShortLinkStatsAccessRecordRespDTO::getUser)
                .toList();
        if(CollectionUtil.isEmpty(userAccessLogsList)){
            return actualResult;
        }
        //TODO: 这里参数分开来，待优化
        List<Map<String, Object>> uvTypeList = shortLinkAccessLogMapper.selectUvTypeByUsers(
                requestParam.getGid(),
                requestParam.getFullShortUrl(),
                requestParam.getStartDate(),
                requestParam.getEndDate(),
                userAccessLogsList
        );
        actualResult.getRecords().forEach(each -> {
            String uvType = uvTypeList.stream()
                    .filter(item -> Objects.equals(each.getUser(), item.get("user")))
                    .findFirst()
                    .map(item -> item.get("uvType"))
                    .map(Object::toString)
                    .orElse("旧访客");
            each.setUvType(uvType);
        });
        return actualResult;
    }
}
