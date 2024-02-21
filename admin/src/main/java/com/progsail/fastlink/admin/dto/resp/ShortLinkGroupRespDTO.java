package com.progsail.fastlink.admin.dto.resp;

import lombok.Data;

/**
 * @author yangfan
 * @version 1.0
 * @description: 短链接分组响应DTO
 * @date 2024/2/17 18:08
 */
@Data
public class ShortLinkGroupRespDTO {
    /**
     * 分组标识
     */
    private String gid;

    /**
     * 分组名称
     */
    private String name;

    /**
     * 分组排序
     */
    private Integer sortOrder;

    /**
     * 分组下短链接数量
     */
    private Integer shortLinkCount;

}
