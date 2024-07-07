package com.progsail.fastlink.admin.remote.impl;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.progsail.fastlink.admin.common.convention.result.Result;
import com.progsail.fastlink.admin.remote.UrlInfoRemoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author yangfan
 * @version 1.0
 * @description: TODO
 * @date 2024/7/7 23:08
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UrlInfoRemoteServiceImpl implements UrlInfoRemoteService {
    @Override
    public Result<String> getUrlTitle(String url) {
        String title = HttpUtil.get("http://127.0.0.1:8001/api/fast-link/project/v1/url/title?url="+url);
        return JSON.parseObject(title, new TypeReference<>() {
        });
    }
}
