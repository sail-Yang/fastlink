package com.progsail.fastlink.admin.dto.resp;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.progsail.fastlink.admin.common.serialize.PhoneDesensitizationSerializer;
import lombok.Data;

/**
 * @author yangfan
 * @version 1.0
 * @description: 用户返回参数响应（脱敏）
 * @date 2024/2/13 21:23
 */
@Data
public class UserRespDTO {
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
    @JsonSerialize(using = PhoneDesensitizationSerializer.class)
    private String phone;

    /**
     * 邮箱
     */
    private String mail;
}
