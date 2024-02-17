package com.progsail.fastlink.admin.dto.req;

import lombok.Data;

/**
 * @author yangfan
 * @version 1.0
 * @description: 短链接分组表保存请求实体
 * @date 2024/2/17 17:26
 */
@Data
public class ShortLinkGroupSaveReqDTO {
    /**
     * 分组名
     */
    private String name;
}
