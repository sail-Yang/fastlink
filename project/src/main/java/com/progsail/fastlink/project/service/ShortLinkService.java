package com.progsail.fastlink.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.progsail.fastlink.project.dao.entity.ShortLinkDO;
import com.progsail.fastlink.project.dto.req.ShortLinkCreateReqDTO;
import com.progsail.fastlink.project.dto.req.ShortLinkPageReqDTO;
import com.progsail.fastlink.project.dto.req.ShortLinkGroupUpdateReqDTO;
import com.progsail.fastlink.project.dto.req.ShortLinkUpdateReqDTO;
import com.progsail.fastlink.project.dto.resp.ShortLinkCreateRespDTO;
import com.progsail.fastlink.project.dto.resp.ShortLinkGroupCountRespDTO;
import com.progsail.fastlink.project.dto.resp.ShortLinkPageRespDTO;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * @author yangfan
 * @version 1.0
 * @description: TODO
 * @date 2024/2/21 20:12
 */
public interface ShortLinkService extends IService<ShortLinkDO> {
    ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam);

    String generateSuffix(ShortLinkCreateReqDTO requestParam);

    IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkPageReqDTO requestParam);

    List<ShortLinkGroupCountRespDTO> listShortLinkGroupCount(List<String> requestParam);

    void updateShortLinkGroup(ShortLinkGroupUpdateReqDTO requestParam);

    void updateShortLink(ShortLinkUpdateReqDTO requestParam);

    void restoreShortLink(String shortUrl, ServletRequest request, ServletResponse response) throws IOException;


}
