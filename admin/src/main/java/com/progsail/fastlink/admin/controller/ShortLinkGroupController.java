package com.progsail.fastlink.admin.controller;

import com.progsail.fastlink.admin.common.convention.result.Result;
import com.progsail.fastlink.admin.common.convention.result.Results;
import com.progsail.fastlink.admin.dto.req.ShortLinkGroupSaveReqDTO;
import com.progsail.fastlink.admin.dto.resp.ShortLinkGroupRespDTO;
import com.progsail.fastlink.admin.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author yangfan
 * @version 1.0
 * @description: 短链接分组控制层
 * @date 2024/2/17 17:28
 */
@RestController
@RequiredArgsConstructor
public class ShortLinkGroupController {

    private final GroupService groupService;

    /**
     * 新增短链接分组
     */
    @PostMapping("/api/fast-link/admin/v1/group/save")
    public Result<Void> save(@RequestBody ShortLinkGroupSaveReqDTO requestParam) {
        groupService.saveGroup(requestParam.getName());
        return Results.success();
    }

    /**
     * 查询短链接分组集合
     */
    @GetMapping("/api/fast-link/admin/v1/group/list")
    public Result<List<ShortLinkGroupRespDTO>> listGroup() {
        return Results.success(groupService.sortList());
    }
}
