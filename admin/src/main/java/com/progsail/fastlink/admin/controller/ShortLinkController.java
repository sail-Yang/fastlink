package com.progsail.fastlink.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.progsail.fastlink.admin.common.convention.result.Result;
import com.progsail.fastlink.admin.dto.req.ShortLinkStatsAccessRecordReqDTO;
import com.progsail.fastlink.admin.dto.resp.ShortLinkStatsAccessRecordRespDTO;
import com.progsail.fastlink.admin.remote.ShortLinkRemoteService;
import com.progsail.fastlink.admin.remote.dto.req.*;
import com.progsail.fastlink.admin.remote.dto.resp.ShortLinkCreateRespDTO;
import com.progsail.fastlink.admin.remote.dto.resp.ShortLinkPageRespDTO;
import com.progsail.fastlink.admin.remote.dto.resp.ShortLinkStatsRespDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yangfan
 * @version 1.0
 * @description: 远程调用中台API
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

    @PostMapping("/api/fast-link/admin/v1/update/group")
    Result<Void> updateShortLinkGroup(@RequestBody ShortLinkGroupUpdateReqDTO requestParam) {
        return shortLinkRemoteService.updateShortLinkGroup(requestParam);
    }

    @PostMapping("/api/fast-link/admin/v1/update/short-link")
    Result<Void> updateShortLink(@RequestBody ShortLinkUpdateReqDTO requestParam) {
        return shortLinkRemoteService.updateShortLink(requestParam);
    }

    @GetMapping("/api/fast-link/admin/v1/stats")
    Result<ShortLinkStatsRespDTO> shortLinkStats(ShortLinkStatsReqDTO requestParam) {
        return shortLinkRemoteService.oneShortLinkStats(requestParam);
    }

    @GetMapping("/api/fast-link/admin/v1/stats/access-record")
    Result<IPage<ShortLinkStatsAccessRecordRespDTO>> shortLinkStatsAccessRecord(ShortLinkStatsAccessRecordReqDTO requestParam) {
        return shortLinkRemoteService.shortLinkStatsAccessRecord(requestParam);
    }
}
