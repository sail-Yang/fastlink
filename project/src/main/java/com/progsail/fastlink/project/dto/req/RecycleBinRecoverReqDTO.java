package com.progsail.fastlink.project.dto.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yangfan
 * @version 1.0
 * @description: TODO
 * @date 2024/7/21 9:49
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecycleBinRecoverReqDTO {
    private  String gid;

    private String fullShortUrl;
}
