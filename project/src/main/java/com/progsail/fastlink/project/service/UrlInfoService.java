package com.progsail.fastlink.project.service;

/**
 * @author yangfan
 * @version 1.0
 * @description: TODO
 * @date 2024/7/7 22:59
 */
public interface UrlInfoService {
    /**
     * 根据url获取该网站标题
     * @param url
     * @return
     */
    String getUrlTitle(String url);
}
