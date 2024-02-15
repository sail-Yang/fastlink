package com.progsail.fastlink.admin.dao.entity;

/**
 * @author yangfan
 * @version 1.0
 * @description: TODO
 * @date 2024/2/13 10:12
 */

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @description t_user
 * @author yangfan
 * @date 2024-02-13
 */
@Data
@TableName("t_user")
public class UserDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    /**
     * id
     */
    private Long id;

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
     * 注销时间戳
     */
    private Long deletionTime;

    /**
     * 创建时间
     */
    @TableField(fill= FieldFill.INSERT)
    private Date createTime;

    /**
     * 修改时间
     */
    @TableField(fill= FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 删除标识 0：未删除 1：已删除
     */
    @TableField(fill= FieldFill.INSERT)
    private Integer delFlag;

}