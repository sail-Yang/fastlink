package com.progsail.fastlink.admin.controller;

import com.progsail.fastlink.admin.common.convention.result.Result;
import com.progsail.fastlink.admin.common.convention.result.Results;
import com.progsail.fastlink.admin.dto.req.ShortLinkGroupSaveReqDTO;
import com.progsail.fastlink.admin.dto.req.ShortLinkGroupUpdateReqDTO;
import com.progsail.fastlink.admin.dto.resp.ShortLinkGroupRespDTO;
import com.progsail.fastlink.admin.service.ShortLinkGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    private final ShortLinkGroupService shortLinkGroupService;

    /**
     * 新增短链接分组
     */
    @PostMapping("/api/fast-link/admin/v1/group/save")
    public Result<Void> save(@RequestBody ShortLinkGroupSaveReqDTO requestParam) {
        shortLinkGroupService.saveGroup(requestParam.getName());
        return Results.success();
    }

    /**
     * 查询短链接分组集合
     */
    @GetMapping("/api/fast-link/admin/v1/group/list")
    public Result<List<ShortLinkGroupRespDTO>> listGroup() {
        return Results.success(shortLinkGroupService.sortList());
    }

    /**
     * 修改分组
     */
    @PutMapping("/api/fast-link/v1/group/update")
    public Result<Void> updateGroup(@RequestBody ShortLinkGroupUpdateReqDTO requestParam) {
        shortLinkGroupService.updateGroup(requestParam);
        return Results.success();
    }
}
