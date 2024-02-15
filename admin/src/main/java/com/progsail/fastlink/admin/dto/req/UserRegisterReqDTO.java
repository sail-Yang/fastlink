package com.progsail.fastlink.admin.dto.req;

import lombok.Data;

/**
 * @author yangfan
 * @version 1.0
 * @description: 用户注册请求实体
 * @date 2024/2/14 16:25
 */
@Data
public class UserRegisterReqDTO {
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
}
