package com.progsail.fastlink.admin.dto.req;

import lombok.Data;

/**
 * @author yangfan
 * @version 1.0
 * @description: 删除短链接分组实体
 * @date 2024/2/21 12:17
 */
@Data
public class ShortLinkGroupDeleteReqDTO {
    /**
     * 分组标识
     */
    private String gid;
}
