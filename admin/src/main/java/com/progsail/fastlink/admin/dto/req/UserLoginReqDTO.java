package com.progsail.fastlink.admin.dto.req;

import lombok.Data;

/**
 * @author yangfan
 * @version 1.0
 * @description: 用户登录请求实体
 * @date 2024/2/15 20:53
 */
@Data
public class UserLoginReqDTO {
    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;
}
