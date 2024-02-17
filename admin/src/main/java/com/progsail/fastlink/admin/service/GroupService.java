package com.progsail.fastlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.progsail.fastlink.admin.dao.entity.GroupDO;

/**
 * @author yangfan
 * @version 1.0
 * @description: 短链接分组服务层
 * @date 2024/2/17 17:18
 */
public interface GroupService extends IService<GroupDO> {
    void saveGroup(String name);

    Boolean hasGid(String gid);
}
