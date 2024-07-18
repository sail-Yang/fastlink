package com.progsail.fastlink.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.progsail.fastlink.admin.common.convention.result.Result;
import com.progsail.fastlink.admin.common.convention.result.Results;
import com.progsail.fastlink.admin.dto.req.RecycleBinSaveReqDTO;
import com.progsail.fastlink.admin.remote.RecycleBinRemoteService;
import com.progsail.fastlink.admin.remote.dto.req.RecycleBinPageReqDTO;
import com.progsail.fastlink.admin.remote.dto.resp.RecycleBinPageRespDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yangfan
 * @version 1.0
 * @description: TODO
 * @date 2024/7/9 14:34
 */
@RestController
@RequiredArgsConstructor
public class RecycleBinController {

    private final RecycleBinRemoteService recycleBinRemoteService;
    @PostMapping("/api/fast-link/admin/v1/recycle-bin/save")
    Result<Void> saveShortLink(@RequestBody RecycleBinSaveReqDTO requestParam) {
        recycleBinRemoteService.saveShortLink(requestParam);
        return Results.success();
    }

    /**
     * 分页查询回收站短链接
     */
    @GetMapping("/api/fast-link/admin/v1/recycle-bin/page")
    public Result<IPage<RecycleBinPageRespDTO>> pageShortLink(RecycleBinPageReqDTO requestParam) {
        return recycleBinRemoteService.pageRecycleBin(requestParam);
    }
}