package com.progsail.fastlink.admin.remote.dto.resp;

import lombok.Data;

/**
 * @author yangfan
 * @version 1.0
 * @description: 短链接分组短链接数量查询返回实体
 * @date 2024/2/23 11:49
 */
@Data
public class ShortLinkGroupCountRespDTO {
    /**
     * 分组标识
     */
    private String gid;

    /**
     * 短链接数量
     */
    private Integer shortLinkCount;
}
