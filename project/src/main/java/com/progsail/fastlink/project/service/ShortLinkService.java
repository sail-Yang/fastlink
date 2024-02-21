package com.progsail.fastlink.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.progsail.fastlink.project.dao.entity.ShortLinkDO;
import com.progsail.fastlink.project.dto.req.ShortLinkCreateReqDTO;
import com.progsail.fastlink.project.dto.resp.ShortLinkCreateRespDTO;

/**
 * @author yangfan
 * @version 1.0
 * @description: TODO
 * @date 2024/2/21 20:12
 */
public interface ShortLinkService extends IService<ShortLinkDO> {
    ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam);

    String generateSuffix(ShortLinkCreateReqDTO requestParam);
}
