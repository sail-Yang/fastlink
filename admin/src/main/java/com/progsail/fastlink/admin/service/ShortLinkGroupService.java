package com.progsail.fastlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.progsail.fastlink.admin.dao.entity.GroupDO;
import com.progsail.fastlink.admin.dto.req.ShortLinkGroupDeleteReqDTO;
import com.progsail.fastlink.admin.dto.req.ShortLinkGroupSortReqDTO;
import com.progsail.fastlink.admin.dto.req.ShortLinkGroupUpdateReqDTO;
import com.progsail.fastlink.admin.dto.resp.ShortLinkGroupRespDTO;

import java.util.List;

/**
 * @author yangfan
 * @version 1.0
 * @description: 短链接分组服务层
 * @date 2024/2/17 17:18
 */
public interface ShortLinkGroupService extends IService<GroupDO> {
    /**
     * 保存分组
     * @param name
     */
    void saveGroup(String name);

    /**
     * 指定用户保存分组
     * @param name
     * @param username
     */
    void saveGroup(String username, String name);

    /**
     * 是否有这个Gid
     * @param gid
     * @return
     */
    Boolean hasGid(String gid);

    /**
     * 获取分组
     * @return
     */
    List<ShortLinkGroupRespDTO> sortList();

    /**
     * 更新分组
     * @param requestParam
     */
    void updateGroup(ShortLinkGroupUpdateReqDTO requestParam);

    /**
     * 删除分组
     * @param requestParam
     */
    void deleteGroup(ShortLinkGroupDeleteReqDTO requestParam);

    /**
     * 分组排序
     * @param requestParam
     */
    void sortGroup(List<ShortLinkGroupSortReqDTO> requestParam);
}

