package com.progsail.fastlink.admin.remote;

import com.progsail.fastlink.admin.common.convention.result.Result;

/**
 * @author yangfan
 * @version 1.0
 * @description: TODO
 * @date 2024/7/7 23:07
 */
public interface UrlInfoRemoteService {
    Result<String> getUrlTitle(String url);
}
