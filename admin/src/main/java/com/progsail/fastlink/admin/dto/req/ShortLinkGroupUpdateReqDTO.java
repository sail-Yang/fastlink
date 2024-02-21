package com.progsail.fastlink.admin.dto.req;

import lombok.Data;

/**
 * @author yangfan
 * @version 1.0
 * @description: 分组更新
 * @date 2024/2/21 12:01
 */
@Data
public class ShortLinkGroupUpdateReqDTO {
    /**
     * 分组标识
     */
    private String gid;

    /**
     * 分组名称
     */
    private String name;
}
