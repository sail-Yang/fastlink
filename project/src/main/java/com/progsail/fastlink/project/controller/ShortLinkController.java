package com.progsail.fastlink.project.controller;

import com.progsail.fastlink.project.common.convention.result.Result;
import com.progsail.fastlink.project.common.convention.result.Results;
import com.progsail.fastlink.project.dto.req.ShortLinkCreateReqDTO;
import com.progsail.fastlink.project.dto.resp.ShortLinkCreateRespDTO;
import com.progsail.fastlink.project.service.ShortLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yangfan
 * @version 1.0
 * @description: 短链接服务控制层
 * @date 2024/2/21 20:14
 */
@RestController
@RequiredArgsConstructor
public class ShortLinkController {
    private final ShortLinkService shortLinkService;


    @PostMapping("/api/fast-link/project/v1/create")
    Result<ShortLinkCreateRespDTO> createShortLink(@RequestBody ShortLinkCreateReqDTO requestParam) {
        return Results.success(shortLinkService.createShortLink(requestParam));
    }
}
