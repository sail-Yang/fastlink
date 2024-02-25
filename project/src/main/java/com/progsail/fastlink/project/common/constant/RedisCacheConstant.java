package com.progsail.fastlink.project.common.constant;

/**
 * @author yangfan
 * @version 1.0
 * @description: 后管 Redis 缓存常量类
 * @date 2024/2/14 17:27
 */
public class RedisCacheConstant {
    /**
     * 短链接跳转前缀 Key
     */
    public static final String GOTO_SHORT_LINK_KEY = "short-link:goto:%s";

    /**
     * 短链接跳转锁前缀 Key
     */
    public static final String LOCK_GOTO_SHORT_LINK_KEY = "short-link:lock:goto:%s";
}
