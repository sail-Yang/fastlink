package com.progsail.fastlink.project.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.progsail.fastlink.project.common.convention.result.Result;
import com.progsail.fastlink.project.common.convention.result.Results;
import com.progsail.fastlink.project.dto.req.ShortLinkCreateReqDTO;
import com.progsail.fastlink.project.dto.req.ShortLinkGroupUpdateReqDTO;
import com.progsail.fastlink.project.dto.req.ShortLinkPageReqDTO;
import com.progsail.fastlink.project.dto.req.ShortLinkUpdateReqDTO;
import com.progsail.fastlink.project.dto.resp.ShortLinkCreateRespDTO;
import com.progsail.fastlink.project.dto.resp.ShortLinkGroupCountRespDTO;
import com.progsail.fastlink.project.dto.resp.ShortLinkPageRespDTO;
import com.progsail.fastlink.project.service.ShortLinkService;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

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

    @GetMapping("/api/fast-link/project/v1/page")
    Result<IPage<ShortLinkPageRespDTO>> getShortLinkPage(ShortLinkPageReqDTO requestParam) {
        return Results.success(shortLinkService.pageShortLink(requestParam));
    }

    @GetMapping("/api/fast-link/project/v1/count")
    Result<List<ShortLinkGroupCountRespDTO>> listShortLinkGroupCount(@RequestParam("gidList") List<String> requestParam) {
        return Results.success(shortLinkService.listShortLinkGroupCount(requestParam));
    }

    @PostMapping("/api/fast-link/project/v1/update/group")
    Result<Void> updateShortLinkGroup(@RequestBody ShortLinkGroupUpdateReqDTO requestParam) {
        shortLinkService.updateShortLinkGroup(requestParam);
        return Results.success();
    }

    @PostMapping("/api/fast-link/project/v1/update/short-link")
    Result<Void> updateShortLink(@RequestBody ShortLinkUpdateReqDTO requestParam) {
        shortLinkService.updateShortLink(requestParam);
        return Results.success();
    }

    @GetMapping("/{short-uri}")
    void restoreShortLink(@PathVariable("short-uri") String shortUrl, ServletRequest request, ServletResponse response) throws IOException {
        shortLinkService.restoreShortLink(shortUrl, request, response);
    }
}
