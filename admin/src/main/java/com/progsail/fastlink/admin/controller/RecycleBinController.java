package com.progsail.fastlink.admin.controller;

import com.progsail.fastlink.admin.common.convention.result.Result;
import com.progsail.fastlink.admin.common.convention.result.Results;
import com.progsail.fastlink.admin.dto.req.RecycleBinSaveReqDTO;
import com.progsail.fastlink.admin.remote.RecycleBinRemoteService;
import lombok.RequiredArgsConstructor;
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
}