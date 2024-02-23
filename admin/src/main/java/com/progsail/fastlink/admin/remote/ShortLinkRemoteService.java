package com.progsail.fastlink.admin.remote;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.progsail.fastlink.admin.common.convention.result.Result;
import com.progsail.fastlink.admin.remote.dto.req.ShortLinkCreateReqDTO;
import com.progsail.fastlink.admin.remote.dto.req.ShortLinkPageReqDTO;
import com.progsail.fastlink.admin.remote.dto.resp.ShortLinkCreateRespDTO;
import com.progsail.fastlink.admin.remote.dto.resp.ShortLinkGroupCountRespDTO;
import com.progsail.fastlink.admin.remote.dto.resp.ShortLinkPageRespDTO;

import java.util.List;

/**
 * @author yangfan
 * @version 1.0
 * @description: 短链接远程调用中台接口
 * @date 2024/2/22 18:55
 */
// TODO: 使用Spring Cloud改造
public interface ShortLinkRemoteService {


    Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO requestParam);

    Result<ShortLinkCreateRespDTO> createShortLink(ShortLinkCreateReqDTO requestParam);

    Result<List<ShortLinkGroupCountRespDTO>> groupShortLinkCount(List<String> requestParam);
}
