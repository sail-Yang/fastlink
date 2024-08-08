package com.progsail.fastlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.Week;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.progsail.fastlink.project.common.convention.exception.ClientException;
import com.progsail.fastlink.project.common.convention.exception.ServiceException;
import com.progsail.fastlink.project.common.enums.VailDateTypeEnum;
import com.progsail.fastlink.project.dao.entity.*;
import com.progsail.fastlink.project.dao.mapper.*;
import com.progsail.fastlink.project.dto.req.ShortLinkCreateReqDTO;
import com.progsail.fastlink.project.dto.req.ShortLinkGroupUpdateReqDTO;
import com.progsail.fastlink.project.dto.req.ShortLinkPageReqDTO;
import com.progsail.fastlink.project.dto.req.ShortLinkUpdateReqDTO;
import com.progsail.fastlink.project.dto.resp.ShortLinkCreateRespDTO;
import com.progsail.fastlink.project.dto.resp.ShortLinkGroupCountRespDTO;
import com.progsail.fastlink.project.dto.resp.ShortLinkPageRespDTO;
import com.progsail.fastlink.project.service.ShortLinkService;
import com.progsail.fastlink.project.util.AccessUtil;
import com.progsail.fastlink.project.util.HashUtil;
import com.progsail.fastlink.project.util.LinkUtil;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.progsail.fastlink.project.common.constant.RedisCacheConstant.*;
import static com.progsail.fastlink.project.common.constant.ShortLinkConstant.AMAP_REMOTE_URL;
import static com.progsail.fastlink.project.common.constant.ShortLinkConstant.UNKNOWN;
import static com.progsail.fastlink.project.util.LinkUtil.isHttpOrHttps;

/**
 * @author yangfan
 * @version 1.0
 * @description: TODO
 * @date 2024/2/21 20:12
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements ShortLinkService {

    private final RBloomFilter<String> shortLinkCreateCachePenetrationBloomFilter;

    private final RedissonClient redissonClient;

    private final ShortLinkGotoMapper shortLinkGotoMapper;

    private final ShortLinkAccessStatsMapper shortLinkAccessStatsMapper;

    private final ShortLinkLocaleStatsMapper shortLinkLocaleStatsMapper;

    private final ShortLinkOsStatsMapper shortLinkOsStatsMapper;

    private final StringRedisTemplate stringRedisTemplate;

    //jsoup连接网页超时时间
    private final int ConnectTimeOutMillis = 5000;

    //高德开放平台访问地区API
    @Value("${short-link.stats.locale.amap-key}")
    private String statsLocaleAmapKey;

    @Override
    public ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam) {
        String shortLinkSuffix = generateSuffix(requestParam);
        String fullShortUrl = StrBuilder.create(requestParam.getDomain())
                .append("/")
                .append(shortLinkSuffix)
                .toString();
        ShortLinkDO shortLinkDO = ShortLinkDO.builder()
                .domain(requestParam.getDomain())
                .originUrl(requestParam.getOriginUrl())
                .gid(requestParam.getGid())
                .createdType(requestParam.getCreatedType())
                .validDateType(requestParam.getValidDateType())
                .validDate(requestParam.getValidDate())
                .describe(requestParam.getDescribe())
                .favicon(getFaviconURL(requestParam.getOriginUrl()))
                .shortUrl(shortLinkSuffix)
                .enableStatus(0)
                .clickNum(0)
                .fullShortUrl(fullShortUrl)
                .build();

        ShortLinkGotoDO shortLinkGotoDO = ShortLinkGotoDO.builder()
                .gid(requestParam.getGid())
                .fullShortUrl(fullShortUrl)
                .build();
        try {
            baseMapper.insert(shortLinkDO);
            shortLinkGotoMapper.insert(shortLinkGotoDO);
        } catch (DuplicateKeyException ex) {
            log.warn("短链接 {} 重复入库", shortLinkDO.getFullShortUrl());
            throw new ServiceException("短链接重复入库");
        }
        // 若短链接永久有效
        if(shortLinkDO.getValidDateType() == 0){
            //缓存预热半小时
            stringRedisTemplate.opsForValue().set(
                    String.format(GOTO_SHORT_LINK_KEY, fullShortUrl),
                    requestParam.getOriginUrl(),
                    30,
                    TimeUnit.MINUTES
            );
        }else{ // 短链接存在有效期
            //计算短链接有效期
            long leftValidTime = LinkUtil.getLinkCacheValidTime(requestParam.getValidDate());
            //若短链接有效期没过
            if(leftValidTime > 0){
                //缓存预热
                stringRedisTemplate.opsForValue().set(
                        String.format(GOTO_SHORT_LINK_KEY, fullShortUrl),
                        requestParam.getOriginUrl(),
                        leftValidTime,
                        TimeUnit.MILLISECONDS
                );
            }else{
                //缓存空值
                stringRedisTemplate.opsForValue().set(String.format(GOTO_IS_NULL_SHORT_LINK_KEY, fullShortUrl), "-", 30, TimeUnit.MINUTES);
            }
        }
        shortLinkCreateCachePenetrationBloomFilter.add(shortLinkDO.getFullShortUrl());
        return ShortLinkCreateRespDTO.builder()
                .fullShortUrl("http://" + shortLinkDO.getFullShortUrl())
                .originUrl(requestParam.getOriginUrl())
                .gid(requestParam.getGid())
                .build();
    }

    @Override
    public String generateSuffix(ShortLinkCreateReqDTO requestParam) {
        int customGenerateCount = 0;
        String shortUrl;
        while(true) {
            if(customGenerateCount > 10){
                throw new ServiceException("短链接频繁生成，请稍后再试");
            }
            String originUrl = requestParam.getOriginUrl();
            //减少冲突概率
            originUrl += UUID.randomUUID().toString();
            shortUrl = HashUtil.hashToBase62(originUrl);
            if(!shortLinkCreateCachePenetrationBloomFilter.contains(requestParam.getDomain() + "/" + shortUrl)){
                break;
            }
            customGenerateCount++;
        }
        return shortUrl;
    }

    @Override
    public IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkPageReqDTO requestParam) {
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid,requestParam.getGid())
                .eq(ShortLinkDO::getDelFlag, 0);
        IPage<ShortLinkDO> page = baseMapper.selectPage(requestParam, queryWrapper);
        return page.convert(each -> {
            ShortLinkPageRespDTO result = BeanUtil.toBean(each, ShortLinkPageRespDTO.class);
            result.setDomain("http://" + result.getDomain());
            return result;
        });
    }

    @Override
    public List<ShortLinkGroupCountRespDTO> listShortLinkGroupCount(List<String> requestParam) {
        QueryWrapper<ShortLinkDO> queryWrapper = Wrappers.query(new ShortLinkDO())
                .select("gid, count(*) AS shortLinkCount")
                .eq("del_flag", 0)
                .eq("enable_status", 0)
                .in("gid", requestParam)
                .groupBy("gid");

        List<Map<String, Object>> maps = baseMapper.selectMaps(queryWrapper);
        return BeanUtil.copyToList(maps, ShortLinkGroupCountRespDTO.class);
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateShortLinkGroup(ShortLinkGroupUpdateReqDTO requestParam) {
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, requestParam.getOldGid())
                .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                .eq(ShortLinkDO::getDelFlag, 0);
        ShortLinkDO shortLink = baseMapper.selectOne(queryWrapper);
        if(shortLink == null) {
            throw new ClientException("短链接不存在");
        }
        baseMapper.delete(queryWrapper);
        shortLink.setGid(requestParam.getNewGid());
        shortLink.setId(null);
        baseMapper.insert(shortLink);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateShortLink(ShortLinkUpdateReqDTO requestParam) {
            LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                    .eq(ShortLinkDO::getGid, requestParam.getGid())
                    .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                    .eq(ShortLinkDO::getDelFlag, 0)
                    .eq(ShortLinkDO::getEnableStatus, 0);
            ShortLinkDO hasShortLinkDO = baseMapper.selectOne(queryWrapper);
            if (hasShortLinkDO == null) {
                throw new ClientException("短链接记录不存在");
            }

            ShortLinkDO shortLinkDO = ShortLinkDO.builder()
                    .domain(hasShortLinkDO.getDomain())
                    .shortUrl(hasShortLinkDO.getShortUrl())
                    .clickNum(hasShortLinkDO.getClickNum())
                    .favicon(hasShortLinkDO.getFavicon())
                    .createdType(hasShortLinkDO.getCreatedType())
                    .gid(requestParam.getGid())
                    .originUrl(requestParam.getOriginUrl())
                    .describe(requestParam.getDescribe())
                    .validDateType(requestParam.getValidDateType())
                    .validDate(requestParam.getValidDate())
                    .build();
            if (Objects.equals(hasShortLinkDO.getGid(), requestParam.getGid())) {
                LambdaUpdateWrapper<ShortLinkDO> updateWrapper = Wrappers.lambdaUpdate(ShortLinkDO.class)
                        .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                        .eq(ShortLinkDO::getGid, requestParam.getGid())
                        .eq(ShortLinkDO::getDelFlag, 0)
                        .eq(ShortLinkDO::getEnableStatus, 0)
                        // 当永久有效时，有效期设置为null
                        .set(Objects.equals(requestParam.getValidDateType(), VailDateTypeEnum.PERMANENT.getType()), ShortLinkDO::getValidDate, null);
                baseMapper.update(shortLinkDO, updateWrapper);
            } else {
                ShortLinkGroupUpdateReqDTO shortLinkGroupUpdateReqDTO = ShortLinkGroupUpdateReqDTO.builder()
                        .fullShortUrl(requestParam.getFullShortUrl())
                        .oldGid(hasShortLinkDO.getGid())
                        .newGid(requestParam.getGid())
                        .build();
                updateShortLinkGroup(shortLinkGroupUpdateReqDTO);
                LambdaUpdateWrapper<ShortLinkDO> updateWrapper = Wrappers.lambdaUpdate(ShortLinkDO.class)
                        .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                        .eq(ShortLinkDO::getGid, requestParam.getGid())
                        .eq(ShortLinkDO::getDelFlag, 0)
                        .eq(ShortLinkDO::getEnableStatus, 0);
                baseMapper.update(shortLinkDO, updateWrapper);
        }

    }

    @SneakyThrows
    @Override
    public void restoreShortLink(String shortUrl, ServletRequest request, ServletResponse response){
        String serverName = request.getServerName();
        String fullShortUrl = serverName + "/" + shortUrl;

        //查该短链接是否已经缓存对应的原始链接
        String originLink = stringRedisTemplate.opsForValue().get(String.format(GOTO_SHORT_LINK_KEY, fullShortUrl));
        if(StrUtil.isNotBlank(originLink)) {
            shortLinkAccessStats(fullShortUrl, null, request, response);
            shortLinkLocaleStats(fullShortUrl, null, request, response);
            shortLinkOsStats(fullShortUrl, null, request, response);
            ((HttpServletResponse) response).sendRedirect(originLink);
            return;
        }

        //缓存穿透：查布隆过滤器，若不存在，说明该短链接不存在于数据库，直接返回
        boolean contains = shortLinkCreateCachePenetrationBloomFilter.contains(fullShortUrl);
        if(!contains){
            ((HttpServletResponse) response).sendRedirect("/page/nofound");
            return;
        }

        //缓存穿透：针对与布隆过滤器的误判，这里查是否有对应的空值，若有对应空值说明不存在
        String gotoIsNullShortLink = stringRedisTemplate.opsForValue().get(String.format(GOTO_IS_NULL_SHORT_LINK_KEY, fullShortUrl));
        if (StrUtil.isNotBlank(gotoIsNullShortLink)) {
            ((HttpServletResponse) response).sendRedirect("/page/nofound");
            return;
        }

        //缓存击穿：双重判定锁
        RLock lock = redissonClient.getLock(String.format(LOCK_GOTO_SHORT_LINK_KEY, fullShortUrl));
        lock.lock();
        try {
            //缓存穿透：先查缓存，针对与布隆过滤器的误判，这里查是否有对应的空值，若有对应空值说明不存在
            gotoIsNullShortLink = stringRedisTemplate.opsForValue().get(String.format(GOTO_IS_NULL_SHORT_LINK_KEY, fullShortUrl));
            if (StrUtil.isNotBlank(gotoIsNullShortLink)) {
                ((HttpServletResponse) response).sendRedirect("/page/nofound");
                return;
            }
            //缓存击穿：先查缓存
            originLink = stringRedisTemplate.opsForValue().get(String.format(GOTO_SHORT_LINK_KEY, fullShortUrl));
            if(StrUtil.isNotBlank(originLink)) {
                shortLinkAccessStats(fullShortUrl, null, request, response);
                shortLinkLocaleStats(fullShortUrl, null, request, response);
                shortLinkOsStats(fullShortUrl, null, request, response);
                ((HttpServletResponse) response).sendRedirect(originLink);
                return;
            }
            LambdaQueryWrapper<ShortLinkGotoDO> shortLinkGotoQueryWrapper = Wrappers.lambdaQuery(ShortLinkGotoDO.class)
                    .eq(ShortLinkGotoDO::getFullShortUrl, fullShortUrl);
            ShortLinkGotoDO shortLinkGotoDO = shortLinkGotoMapper.selectOne(shortLinkGotoQueryWrapper);
            if(shortLinkGotoDO == null) {
                // 短链接不存在，缓存对应空值
                stringRedisTemplate.opsForValue().set(String.format(GOTO_IS_NULL_SHORT_LINK_KEY, fullShortUrl), "-", 30, TimeUnit.MINUTES);
                ((HttpServletResponse) response).sendRedirect("/page/nofound");
                return;
            }
            LambdaQueryWrapper<ShortLinkDO> shortLinkDOQueryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                    .eq(ShortLinkDO::getGid, shortLinkGotoDO.getGid())
                    .eq(ShortLinkDO::getFullShortUrl, fullShortUrl)
                    .eq(ShortLinkDO::getDelFlag, 0)
                    .eq(ShortLinkDO::getEnableStatus, 0);
            ShortLinkDO shortLinkDO = baseMapper.selectOne(shortLinkDOQueryWrapper);
            if(shortLinkDO != null) {
                // 判断短链接是否过期
                if(shortLinkDO.getValidDate() != null  && shortLinkDO.getValidDate().before(new Date())){
                    stringRedisTemplate.opsForValue().set(String.format(GOTO_IS_NULL_SHORT_LINK_KEY, fullShortUrl), "-", 30, TimeUnit.MINUTES);
                    ((HttpServletResponse) response).sendRedirect("/page/nofound");
                    return;
                }
                stringRedisTemplate.opsForValue().set(
                        String.format(GOTO_SHORT_LINK_KEY, fullShortUrl),
                        shortLinkDO.getOriginUrl(),
                        LinkUtil.getLinkCacheValidTime(shortLinkDO.getValidDate()),
                        TimeUnit.MILLISECONDS
                );
                shortLinkAccessStats(fullShortUrl, shortLinkDO.getGid(), request, response);
                shortLinkLocaleStats(fullShortUrl, shortLinkDO.getGid(), request, response);
                shortLinkOsStats(fullShortUrl, null, request, response);
                ((HttpServletResponse) response).sendRedirect(shortLinkDO.getOriginUrl());
            }else{
                ((HttpServletResponse) response).sendRedirect("/page/nofound");
                return;
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * 访问监控统计
     * @param fullShortUrl
     * @param gid
     * @param request
     * @param response
     */
    private void shortLinkAccessStats(String fullShortUrl, String gid, ServletRequest request, ServletResponse response) {
        AtomicBoolean uvFirstFlag = new AtomicBoolean();
        AtomicBoolean uipFirstFlag = new AtomicBoolean();
        Cookie[] cookies = ((HttpServletRequest) request).getCookies();
        try {
            Runnable addResponseCookieTask = () -> {
                String uv = UUID.fastUUID().toString();
                Cookie uvCookie = new Cookie("uv", uv);
                uvCookie.setMaxAge(60 * 60 * 24 * 30);
                //不添加path就会对整个域名有效
                uvCookie.setPath(StrUtil.sub(fullShortUrl, fullShortUrl.indexOf("/"), fullShortUrl.length()));
                ((HttpServletResponse) response).addCookie(uvCookie);
                uvFirstFlag.set(Boolean.TRUE);
                stringRedisTemplate.opsForSet().add("short-link:stats:uv:" + fullShortUrl, uv);
            };
            if (ArrayUtil.isNotEmpty(cookies)) {
                Arrays.stream(cookies)
                        .filter(each -> Objects.equals(each.getName(), "uv"))
                        .findFirst()
                        .map(Cookie::getValue)
                        .ifPresentOrElse(each -> {
                            Long added = stringRedisTemplate.opsForSet().add("short-link:stats:uv:" + fullShortUrl, each);
                            uvFirstFlag.set(added != null && added > 0L);
                        }, addResponseCookieTask);
            } else {
                addResponseCookieTask.run();
            }
            if (StrUtil.isBlank(gid)) {
                LambdaQueryWrapper<ShortLinkGotoDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkGotoDO.class)
                        .eq(ShortLinkGotoDO::getFullShortUrl, fullShortUrl);
                ShortLinkGotoDO shortLinkGotoDO = shortLinkGotoMapper.selectOne(queryWrapper);
                gid = shortLinkGotoDO.getGid();
            }
            // ip统计
            String ip = AccessUtil.getIpAddr((HttpServletRequest) request);
            if(!UNKNOWN.equalsIgnoreCase(ip)){
                Long added = stringRedisTemplate.opsForSet().add("short-link:stats:uip:" + fullShortUrl, ip);
                uipFirstFlag.set(added != null && added > 0L);
            }

            Date nowDate = new Date();
            int hour = DateUtil.hour(nowDate, true);
            Week week = DateUtil.dayOfWeekEnum(nowDate);
            int weekValue = week.getIso8601Value();
            ShortLinkAccessStatsDO linkAccessStatsDO = ShortLinkAccessStatsDO.builder()
                    .pv(1)
                    .uv(uvFirstFlag.get() ? 1 : 0)
                    .uip(uipFirstFlag.get() ? 1 : 0)
                    .hour(hour)
                    .weekday(weekValue)
                    .fullShortUrl(fullShortUrl)
                    .gid(gid)
                    .date(new Date())
                    .build();
            shortLinkAccessStatsMapper.shortLinkAccessStats(linkAccessStatsDO);
        } catch (Throwable ex) {
            log.error("短链接访问量统计异常", ex);
        }
    }

    /**
     * 访问地区统计
     * @param fullShortUrl
     * @param gid
     * @param request
     * @param response
     */
    private void shortLinkLocaleStats(String fullShortUrl, String gid, ServletRequest request, ServletResponse response) {
        // ip统计
        String ip = AccessUtil.getIpAddr((HttpServletRequest) request);
        if(UNKNOWN.equalsIgnoreCase(ip)){
            return;
        }
        try {
            if (StrUtil.isBlank(gid)) {
                LambdaQueryWrapper<ShortLinkGotoDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkGotoDO.class)
                        .eq(ShortLinkGotoDO::getFullShortUrl, fullShortUrl);
                ShortLinkGotoDO shortLinkGotoDO = shortLinkGotoMapper.selectOne(queryWrapper);
                gid = shortLinkGotoDO.getGid();
            }

            Map<String, Object> localeParamMap = new HashMap<>();
            localeParamMap.put("key", statsLocaleAmapKey);
            localeParamMap.put("ip", ip);
            String localeResultStr = HttpUtil.get(AMAP_REMOTE_URL, localeParamMap);
            JSONObject localeResultObj = JSON.parseObject(localeResultStr);
            String infoCode = localeResultObj.getString("infocode");
            if (StrUtil.isNotBlank(infoCode) && StrUtil.equals(infoCode, "10000")) {
                String province = localeResultObj.getString("province");
                boolean unknownFlag = StrUtil.equals(province, "[]");
                ShortLinkLocaleStatsDO linkLocaleStatsDO = ShortLinkLocaleStatsDO.builder()
                        .province(unknownFlag ? UNKNOWN : province)
                        .city(unknownFlag ? UNKNOWN : localeResultObj.getString("city"))
                        .adcode(unknownFlag ? UNKNOWN : localeResultObj.getString("adcode"))
                        .cnt(1)
                        .fullShortUrl(fullShortUrl)
                        .country("中国")
                        .gid(gid)
                        .date(new Date())
                        .build();
                shortLinkLocaleStatsMapper.shortLinkLocaleState(linkLocaleStatsDO);
            }
        }catch (Throwable ex) {
            log.error("短链接地区访问量统计异常", ex);
        }

    }

    /**
     * 操作系统统计
     * @param fullShortUrl
     * @param gid
     * @param request
     * @param response
     */
    private void shortLinkOsStats(String fullShortUrl, String gid, ServletRequest request, ServletResponse response) {
        // os统计
        String os = AccessUtil.getOs((HttpServletRequest) request);
        if(UNKNOWN.equalsIgnoreCase(os)){
            return;
        }
        try {
            if (StrUtil.isBlank(gid)) {
                LambdaQueryWrapper<ShortLinkGotoDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkGotoDO.class)
                        .eq(ShortLinkGotoDO::getFullShortUrl, fullShortUrl);
                ShortLinkGotoDO shortLinkGotoDO = shortLinkGotoMapper.selectOne(queryWrapper);
                gid = shortLinkGotoDO.getGid();
            }

            ShortLinkOsStatsDO linkOsStatsDO = ShortLinkOsStatsDO.builder()
                    .os(os)
                    .cnt(1)
                    .fullShortUrl(fullShortUrl)
                    .gid(gid)
                    .date(new Date())
                    .build();
            shortLinkOsStatsMapper.shortLinkOsState(linkOsStatsDO);
        }catch (Throwable ex) {
            log.error("短链接操作系统访问量统计异常", ex);
        }

    }

    /**
     * 获取网站图标链接
     * @param originURL
     * @return
     */
    private String getFaviconURL(String originURL) {
        originURL = StrUtil.trimToEmpty(originURL);
        //不携带协议就拼接
        if(!isHttpOrHttps(originURL)){
            originURL = StrUtil.format("https://{}",originURL);
        }
        URL url = null;
        try {
            url = new URL(originURL);
        } catch (MalformedURLException e) {
            return null;
        }
        String hostUrl = url.getProtocol().concat("://").concat(url.getHost());
        Document document = null;
        try {
            document = Jsoup.connect(hostUrl).timeout(ConnectTimeOutMillis).get();
        } catch (IOException e) {
            return null;
        }
        // 筛选包含favicon图标的link标签
        Elements title = document.select("link[type=image/x-icon]");
        title = ObjectUtil.isEmpty(title) ? document.select("link[rel$=icon]") : title;
        // 获取favicon路径
        String href = title.attr("href");
        // 假设获取到的favicon路径已经包含了域名，则直接返回
        if (isHttpOrHttps(href) && StrUtil.containsAny(href, "favicon")) {
            return href;
        }
        // 拼接favicon的访问链接
        return StrUtil.format("{}/{}", hostUrl, StrUtil.removePrefix(href, "/"));
    }
}
