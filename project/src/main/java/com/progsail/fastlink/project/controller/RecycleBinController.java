package com.progsail.fastlink.project.controller;

import com.progsail.fastlink.project.common.convention.result.Result;
import com.progsail.fastlink.project.common.convention.result.Results;
import com.progsail.fastlink.project.dto.req.RecycleBinSaveReqDTO;
import com.progsail.fastlink.project.service.RecycleBinService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yangfan
 * @version 1.0
 * @description: 回收站管理控制器
 * @date 2024/7/9 11:59
 */
@RestController
@RequiredArgsConstructor
public class RecycleBinController {

    private final RecycleBinService recycleBinService;
    @PostMapping("/api/fast-link/project/v1/recycle-bin/save")
    Result<Void> saveShortLink(@RequestBody RecycleBinSaveReqDTO requestParam) {
        recycleBinService.saveShortLink(requestParam);
        return Results.success();
    }
}
