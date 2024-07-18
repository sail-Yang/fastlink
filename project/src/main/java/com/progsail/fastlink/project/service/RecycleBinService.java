package com.progsail.fastlink.project.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.progsail.fastlink.project.dao.entity.ShortLinkDO;
import com.progsail.fastlink.project.dto.req.RecycleBinPageReqDTO;
import com.progsail.fastlink.project.dto.req.RecycleBinSaveReqDTO;
import com.progsail.fastlink.project.dto.resp.RecycleBinPageRespDTO;

/**
 * @author yangfan
 * @version 1.0
 * @description: 回收站管理服务
 * @date 2024/7/9 12:00
 */
public interface RecycleBinService extends IService<ShortLinkDO> {
    void saveShortLink(RecycleBinSaveReqDTO requestParam);

    IPage<RecycleBinPageRespDTO> pageRecycleBin(RecycleBinPageReqDTO requestParam);
}
