package com.progsail.fastlink.admin.remote;

import com.progsail.fastlink.admin.common.convention.result.Result;
import com.progsail.fastlink.admin.dto.req.RecycleBinSaveReqDTO;

/**
 * @author yangfan
 * @version 1.0
 * @description: 回收站管理调用中台接口
 * @date 2024/7/9 14:35
 */
public interface RecycleBinRemoteService {
    Result<Void> saveShortLink(RecycleBinSaveReqDTO requestParam);
}
