package com.progsail.fastlink.admin.controller;

import com.progsail.fastlink.admin.common.convention.result.Result;
import com.progsail.fastlink.admin.common.convention.result.Results;
import com.progsail.fastlink.admin.remote.UrlInfoRemoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yangfan
 * @version 1.0
 * @description: 网址信息
 * @date 2024/7/7 22:58
 */
@RestController
@RequiredArgsConstructor
public class UrlInfoController {
    private final UrlInfoRemoteService urlInfoRemoteService;

    @GetMapping("/api/fast-link/admin/v1/url/title")
    public Result<String> getUrlTitle(@RequestParam("url") String url) {
        return urlInfoRemoteService.getUrlTitle(url);
    }

}
