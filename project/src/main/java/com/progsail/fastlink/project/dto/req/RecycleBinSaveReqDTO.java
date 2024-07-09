package com.progsail.fastlink.project.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yangfan
 * @version 1.0
 * @description: TODO
 * @date 2024/7/9 12:06
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecycleBinSaveReqDTO {
    private String gid;

    private String fullShortUrl;
}

