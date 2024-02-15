package com.progsail.fastlink.admin.common.constant;

/**
 * @author yangfan
 * @version 1.0
 * @description: 后管 Redis 缓存常量类
 * @date 2024/2/14 17:27
 */
public class RedisCacheConstant {
    /**
     * 用户注册分布式锁
     */
    public static final String LOCK_USER_REGISTER_KEY = "fast-link:lock_user-register:";

    /**
     * 用户登录缓存标识
     */
    public static final String USER_LOGIN_KEY = "fast-link:login:";
}
