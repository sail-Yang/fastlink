package com.progsail.fastlink.admin.dto.req;


import lombok.Data;

/**
 * @author yangfan
 * @version 1.0
 * @description: 用户更新
 * @date 2024/2/15 19:24
 */
@Data
public class UserUpdateReqDTO {
    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String mail;

    /**
     * 用户Token凭证
     */
    private String token;
}

