package com.progsail.fastlink.project.dto.req;

import lombok.Builder;
import lombok.Data;

/**
 * @author yangfan
 * @version 1.0
 * @description: 修改短链接分组请求实体
 * @date 2024/2/23 18:44
 */
@Data
@Builder
public class ShortLinkGroupUpdateReqDTO {

    /**
     * 短链接
     */
    private String fullShortUrl;

    /**
     * 新的分组标识
     */
    private String newGid;

    /**
     * 旧的分组标识
     */
    private String oldGid;
}
