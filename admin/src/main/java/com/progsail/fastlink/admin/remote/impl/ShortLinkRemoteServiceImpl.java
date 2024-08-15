package com.progsail.fastlink.admin.remote.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.progsail.fastlink.admin.common.convention.result.Result;
import com.progsail.fastlink.admin.dto.req.ShortLinkStatsAccessRecordReqDTO;
import com.progsail.fastlink.admin.dto.resp.ShortLinkStatsAccessRecordRespDTO;
import com.progsail.fastlink.admin.remote.ShortLinkRemoteService;
import com.progsail.fastlink.admin.remote.dto.req.*;
import com.progsail.fastlink.admin.remote.dto.resp.ShortLinkCreateRespDTO;
import com.progsail.fastlink.admin.remote.dto.resp.ShortLinkGroupCountRespDTO;
import com.progsail.fastlink.admin.remote.dto.resp.ShortLinkPageRespDTO;
import com.progsail.fastlink.admin.remote.dto.resp.ShortLinkStatsRespDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yangfan
 * @version 1.0
 * @description: TODO
 * @date 2024/2/22 20:00
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ShortLinkRemoteServiceImpl implements ShortLinkRemoteService {
    private final RestTemplate restTemplate;


    @Override
    public Result<IPage<ShortLinkPageRespDTO>> pageShortLink(ShortLinkPageReqDTO requestParam) {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("gid", requestParam.getGid());
        requestMap.put("current", requestParam.getCurrent());
        requestMap.put("size", requestParam.getSize());
        String resultPageStr = HttpUtil.get("http://127.0.0.1:8001/api/fast-link/project/v1/page",requestMap);
        return JSON.parseObject(resultPageStr, new TypeReference<>() {
        });
    }

    @Override
    public Result<ShortLinkCreateRespDTO> createShortLink(ShortLinkCreateReqDTO requestParam) {

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ShortLinkCreateReqDTO> r = new HttpEntity<>(requestParam, requestHeaders);

        String url = "http://127.0.0.1:8001/api/fast-link/project/v1/create";
        String resultPageStr = restTemplate.postForObject(url, r, String.class);
//          hutool.HttpUtil方法
//        String resultPageStr = HttpUtil
//                .createPost("http://127.0.0.1:8001/api/fast-link/project/v1/create")
//                .addHeaders(headers)
//                .form(requestMap)
//                .execute().body();
        return JSON.parseObject(resultPageStr, new TypeReference<>() {
        });
    }

    @Override
    public Result<List<ShortLinkGroupCountRespDTO>> groupShortLinkCount(List<String> requestParam) {
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("gidList", requestParam);
        String resultPageStr = HttpUtil.get("http://127.0.0.1:8001/api/fast-link/project/v1/count",requestMap);
        return JSON.parseObject(resultPageStr, new TypeReference<>() {
        });
    }

    @Override
    public Result<Void> updateShortLinkGroup(ShortLinkGroupUpdateReqDTO requestParam) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ShortLinkGroupUpdateReqDTO> r = new HttpEntity<>(requestParam, requestHeaders);
        String url = "http://127.0.0.1:8001/api/fast-link/project/v1/update/group";
        String resultPageStr = restTemplate.postForObject(url, r, String.class);
//          hutool.HttpUtil方法
//        String resultPageStr = HttpUtil
//                .createPost("http://127.0.0.1:8001/api/fast-link/project/v1/create")
//                .addHeaders(headers)
//                .form(requestMap)
//                .execute().body();
        return JSON.parseObject(resultPageStr, new TypeReference<>() {
        });
    }

    @Override
    public Result<Void> updateShortLink(ShortLinkUpdateReqDTO requestParam) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ShortLinkUpdateReqDTO> r = new HttpEntity<>(requestParam, requestHeaders);
        String url = "http://127.0.0.1:8001/api/fast-link/project/v1/update/short-link";
        String resultPageStr = restTemplate.postForObject(url, r, String.class);
        return JSON.parseObject(resultPageStr, new TypeReference<>() {
        });
    }

    @Override
    public Result<ShortLinkStatsRespDTO> oneShortLinkStats(ShortLinkStatsReqDTO requestParam) {
        String resultBodyStr = HttpUtil.get("http://127.0.0.1:8001/api/fast-link/project/v1/stats", BeanUtil.beanToMap(requestParam));
        return JSON.parseObject(resultBodyStr, new TypeReference<>() {
        });
    }

    @Override
    public Result<IPage<ShortLinkStatsAccessRecordRespDTO>> shortLinkStatsAccessRecord(ShortLinkStatsAccessRecordReqDTO requestParam) {
        String resultBodyStr = HttpUtil.get("http://127.0.0.1:8001/api/fast-link/project/v1/stats/access-record", BeanUtil.beanToMap(requestParam));
        return JSON.parseObject(resultBodyStr, new TypeReference<>() {
        });
    }
}
