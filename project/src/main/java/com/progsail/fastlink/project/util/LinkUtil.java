package com.progsail.fastlink.project.util;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;

import java.util.Date;
import java.util.Optional;

import static com.progsail.fastlink.project.common.constant.ShortLinkConstant.DEFAULT_CACHE_VALID_TIME;

/**
 * @author yangfan
 * @version 1.0
 * @description: 短链接工具类
 * @date 2024/7/3 14:59
 */
public class LinkUtil {
    /**
     * 获取短链接缓存有效期时间
     * @param validDate 有效期时间
     * @return 有限期时间戳
     */
    public static long getLinkCacheValidTime(Date validDate){
        return Optional.ofNullable(validDate)
                .map(each -> DateUtil.between(new Date(), each, DateUnit.MS, false))
                .orElse(DEFAULT_CACHE_VALID_TIME);
    }

}
