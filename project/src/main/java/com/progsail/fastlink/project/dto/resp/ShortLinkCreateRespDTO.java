package com.progsail.fastlink.project.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yangfan
 * @version 1.0
 * @description: 短链接创建返回实体
 * @date 2024/2/21 20:52
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShortLinkCreateRespDTO {
    /**
     * 分组信息
     */
    private String gid;

    /**
     * 原始链接
     */
    private String originUrl;

    /**
     * 短链接
     */
    private String fullShortUrl;
}
