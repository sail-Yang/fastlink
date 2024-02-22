package com.progsail.fastlink.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.progsail.fastlink.admin.common.convention.result.Result;
import com.progsail.fastlink.admin.remote.ShortLinkRemoteService;
import com.progsail.fastlink.admin.remote.dto.req.ShortLinkCreateReqDTO;
import com.progsail.fastlink.admin.remote.dto.req.ShortLinkPageReqDTO;
import com.progsail.fastlink.admin.remote.dto.resp.ShortLinkCreateRespDTO;
import com.progsail.fastlink.admin.remote.dto.resp.ShortLinkPageRespDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yangfan
 * @version 1.0
 * @description: TODO
 * @date 2024/2/22 19:01
 */
@RestController
@RequiredArgsConstructor
public class ShortLinkController {

    private final ShortLinkRemoteService shortLinkRemoteService;

    @GetMapping("/api/fast-link/admin/v1/page")
    Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO requestParam) {
        return shortLinkRemoteService.pageShortLink(requestParam);
    }

    @PostMapping("/api/fast-link/admin/v1/create")
    Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody  ShortLinkCreateReqDTO requestParam) {
        return shortLinkRemoteService.createShortLink(requestParam);
    }
}
