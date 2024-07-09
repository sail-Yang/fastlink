package com.progsail.fastlink.admin.remote.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.progsail.fastlink.admin.common.convention.result.Result;
import com.progsail.fastlink.admin.dto.req.RecycleBinSaveReqDTO;
import com.progsail.fastlink.admin.remote.RecycleBinRemoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @author yangfan
 * @version 1.0
 * @description: TODO
 * @date 2024/7/9 14:35
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RecycleBinRemoteServiceImpl implements RecycleBinRemoteService {
    private final RestTemplate restTemplate;

    @Override
    public Result<Void> saveShortLink(RecycleBinSaveReqDTO requestParam) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<RecycleBinSaveReqDTO> r = new HttpEntity<>(requestParam, requestHeaders);

        String url = "http://127.0.0.1:8001/api/fast-link/project/v1/recycle-bin/save";
        String resultPageStr = restTemplate.postForObject(url, r, String.class);
        return JSON.parseObject(resultPageStr, new TypeReference<>() {
        });
    }
}
