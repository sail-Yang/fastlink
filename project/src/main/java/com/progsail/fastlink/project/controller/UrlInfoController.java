package com.progsail.fastlink.project.controller;

import com.progsail.fastlink.project.common.convention.result.Result;
import com.progsail.fastlink.project.common.convention.result.Results;
import com.progsail.fastlink.project.service.UrlInfoService;
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
    private final UrlInfoService urlInfoService;

    @GetMapping("/api/fast-link/project/v1/url/title")
    public Result<String> getUrlTitle(@RequestParam("url") String url) {
        return Results.success(urlInfoService.getUrlTitle(url));
    }

}
