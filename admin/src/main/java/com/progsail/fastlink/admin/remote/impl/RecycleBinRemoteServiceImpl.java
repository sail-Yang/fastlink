package com.progsail.fastlink.admin.remote.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.progsail.fastlink.admin.common.biz.user.UserContext;
import com.progsail.fastlink.admin.common.convention.exception.ServiceException;
import com.progsail.fastlink.admin.common.convention.result.Result;
import com.progsail.fastlink.admin.dao.entity.GroupDO;
import com.progsail.fastlink.admin.dao.mapper.GroupMapper;
import com.progsail.fastlink.admin.dto.req.RecycleBinSaveReqDTO;
import com.progsail.fastlink.admin.remote.RecycleBinRemoteService;
import com.progsail.fastlink.admin.remote.dto.req.RecycleBinPageReqDTO;
import com.progsail.fastlink.admin.remote.dto.req.RecycleBinRecoverReqDTO;
import com.progsail.fastlink.admin.remote.dto.resp.RecycleBinPageRespDTO;
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
 * @date 2024/7/9 14:35
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RecycleBinRemoteServiceImpl implements RecycleBinRemoteService {
    private final RestTemplate restTemplate;

    private final GroupMapper groupMapper;

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

    @Override
    public Result<IPage<RecycleBinPageRespDTO>> pageRecycleBin(RecycleBinPageReqDTO requestParam) {
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .eq(GroupDO::getDelFlag, 0);
        List<GroupDO> groupDOList = groupMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(groupDOList)) {
            throw new ServiceException("用户无分组信息");
        }
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("gidList", groupDOList.stream().map(GroupDO::getGid).toList());
        requestMap.put("current", requestParam.getCurrent());
        requestMap.put("size", requestParam.getSize());
        String resultPageStr = HttpUtil.get("http://127.0.0.1:8001/api/fast-link/project/v1/recycle-bin/page",requestMap);
        return JSON.parseObject(resultPageStr, new TypeReference<>() {
        });
    }

    @Override
    public Result<Void> recoverRecycleBin(RecycleBinRecoverReqDTO requestParam) {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<RecycleBinRecoverReqDTO> r = new HttpEntity<>(requestParam, requestHeaders);

        String url = "http://127.0.0.1:8001/api/fast-link/project/v1/recycle-bin/recover";
        String resultPageStr = restTemplate.postForObject(url, r, String.class);
        return JSON.parseObject(resultPageStr, new TypeReference<>() {
        });
    }
}
