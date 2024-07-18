package com.progsail.fastlink.project.dto.resp;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author yangfan
 * @version 1.0
 * @description: 回收站分页响应实体
 * @date 2024/7/18 20:10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecycleBinPageRespDTO {
    /**
     * 域名
     */
    private String domain;

    /**
     * 短链接
     */
    private String shortUrl;

    /**
     * 完整短链接
     */
    private String fullShortUrl;

    /**
     * 原始链接
     */
    private String originUrl;

    /**
     * 点击量
     */
    private Integer clickNum;

    /**
     * 分组标识
     */
    private String gid;


    /**
     * 创建类型 0：控制台 1：接口
     */
    private Integer createdType;

    /**
     * 有效期类型 0：永久有效 1：用户自定义
     */
    private Integer validDateType;

    /**
     * 有效期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date validDate;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 描述
     */
    @TableField("`describe`")
    private String describe;

    /**
     * 图标链接
     */
    private String favicon;
}
