package com.progsail.fastlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.progsail.fastlink.project.dao.entity.ShortLinkDO;
import com.progsail.fastlink.project.dao.mapper.ShortLinkMapper;
import com.progsail.fastlink.project.dto.req.ShortLinkCreateReqDTO;
import com.progsail.fastlink.project.dto.resp.ShortLinkCreateRespDTO;
import com.progsail.fastlink.project.service.ShortLinkService;
import com.progsail.fastlink.project.util.HashUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author yangfan
 * @version 1.0
 * @description: TODO
 * @date 2024/2/21 20:12
 */
@Service
@RequiredArgsConstructor
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements ShortLinkService {
    @Override
    public ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam) {
        String shortLinkSuffix = generateSuffix(requestParam);
        ShortLinkDO shortLinkDO = BeanUtil.toBean(requestParam, ShortLinkDO.class);
        shortLinkDO.setShortUrl(shortLinkSuffix);
        shortLinkDO.setEnableStatus(0);
        shortLinkDO.setClickNum(0);
        shortLinkDO.setFullShortUrl(requestParam.getDomain() + "/" + shortLinkSuffix);
        baseMapper.insert(shortLinkDO);
        return ShortLinkCreateRespDTO.builder()
                .fullShortUrl(shortLinkDO.getFullShortUrl())
                .originUrl(requestParam.getOriginUrl())
                .gid(requestParam.getGid())
                .build();
    }

    @Override
    public String generateSuffix(ShortLinkCreateReqDTO requestParam) {
        String originUrl = requestParam.getOriginUrl();
        return HashUtil.hashToBase62(originUrl);
    }
}
