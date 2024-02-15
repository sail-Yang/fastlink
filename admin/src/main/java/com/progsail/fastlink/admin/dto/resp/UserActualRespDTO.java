package com.progsail.fastlink.admin.dto.resp;

import lombok.Data;

/**
 * @author yangfan
 * @version 1.0
 * @description: 用户返回参数响应（无脱敏）
 * @date 2024/2/13 21:23
 */
@Data
public class UserActualRespDTO {
    /**
     * id
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

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
