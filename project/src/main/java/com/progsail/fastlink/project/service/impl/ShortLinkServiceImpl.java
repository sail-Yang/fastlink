package com.progsail.fastlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.progsail.fastlink.project.common.convention.exception.ClientException;
import com.progsail.fastlink.project.common.convention.exception.ServiceException;
import com.progsail.fastlink.project.common.enums.VailDateTypeEnum;
import com.progsail.fastlink.project.dao.entity.ShortLinkDO;
import com.progsail.fastlink.project.dao.entity.ShortLinkGotoDO;
import com.progsail.fastlink.project.dao.mapper.ShortLinkGotoMapper;
import com.progsail.fastlink.project.dao.mapper.ShortLinkMapper;
import com.progsail.fastlink.project.dto.req.ShortLinkCreateReqDTO;
import com.progsail.fastlink.project.dto.req.ShortLinkGroupUpdateReqDTO;
import com.progsail.fastlink.project.dto.req.ShortLinkPageReqDTO;
import com.progsail.fastlink.project.dto.req.ShortLinkUpdateReqDTO;
import com.progsail.fastlink.project.dto.resp.ShortLinkCreateRespDTO;
import com.progsail.fastlink.project.dto.resp.ShortLinkGroupCountRespDTO;
import com.progsail.fastlink.project.dto.resp.ShortLinkPageRespDTO;
import com.progsail.fastlink.project.service.ShortLinkService;
import com.progsail.fastlink.project.util.HashUtil;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.progsail.fastlink.project.common.constant.RedisCacheConstant.*;

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

    private final StringRedisTemplate stringRedisTemplate;

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
                .favicon(requestParam.getFavicon())
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
            originUrl += UUID.randomUUID().toString();//减少冲突概率
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
//        return page.convert(each -> BeanUtil.toBean(each, ShortLinkPageRespDTO.class));
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
            ((HttpServletResponse) response).sendRedirect(originLink);
        }

        //缓存穿透：查布隆过滤器，若不存在，说明该短链接不存在于数据库，直接返回
        boolean contains = shortLinkCreateCachePenetrationBloomFilter.contains(fullShortUrl);
        if(!contains){
            return;
        }

        //缓存穿透：针对与布隆过滤器的误判，这里查是否有对应的空值，若有对应空值说明不存在
        String gotoIsNullShortLink = stringRedisTemplate.opsForValue().get(String.format(GOTO_IS_NULL_SHORT_LINK_KEY, fullShortUrl));
        if (StrUtil.isNotBlank(gotoIsNullShortLink)) {
            return;
        }

        //缓存击穿：双重判定锁
        RLock lock = redissonClient.getLock(String.format(LOCK_GOTO_SHORT_LINK_KEY, fullShortUrl));
        lock.lock();
        try {
            //缓存穿透：先查缓存，针对与布隆过滤器的误判，这里查是否有对应的空值，若有对应空值说明不存在
            gotoIsNullShortLink = stringRedisTemplate.opsForValue().get(String.format(GOTO_IS_NULL_SHORT_LINK_KEY, fullShortUrl));
            if (StrUtil.isNotBlank(gotoIsNullShortLink)) {
                return;
            }
            //缓存击穿：先查缓存
            originLink = stringRedisTemplate.opsForValue().get(String.format(GOTO_SHORT_LINK_KEY, fullShortUrl));
            if(StrUtil.isNotBlank(originLink)) {
                ((HttpServletResponse) response).sendRedirect(originLink);
                return;
            }
            LambdaQueryWrapper<ShortLinkGotoDO> shortLinkGotoQueryWrapper = Wrappers.lambdaQuery(ShortLinkGotoDO.class)
                    .eq(ShortLinkGotoDO::getFullShortUrl, fullShortUrl);
            ShortLinkGotoDO shortLinkGotoDO = shortLinkGotoMapper.selectOne(shortLinkGotoQueryWrapper);
            if(shortLinkGotoDO == null) {
                // 短链接不存在，缓存对应空值
                stringRedisTemplate.opsForValue().set(String.format(GOTO_IS_NULL_SHORT_LINK_KEY, fullShortUrl), "-", 30, TimeUnit.MINUTES);
                return;
            }
            LambdaQueryWrapper<ShortLinkDO> shortLinkDOQueryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                    .eq(ShortLinkDO::getGid, shortLinkGotoDO.getGid())
                    .eq(ShortLinkDO::getFullShortUrl, fullShortUrl)
                    .eq(ShortLinkDO::getDelFlag, 0)
                    .eq(ShortLinkDO::getEnableStatus, 0);
            ShortLinkDO shortLinkDO = baseMapper.selectOne(shortLinkDOQueryWrapper);
            if(shortLinkDO != null) {
                stringRedisTemplate.opsForValue().set(String.format(GOTO_SHORT_LINK_KEY, fullShortUrl), shortLinkDO.getOriginUrl());
                ((HttpServletResponse) response).sendRedirect(shortLinkDO.getOriginUrl());
            }
        } finally {
            lock.unlock();
        }
    }
}
