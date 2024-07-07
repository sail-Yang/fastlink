package com.progsail.fastlink.project.service.impl;

import com.progsail.fastlink.project.service.UrlInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author yangfan
 * @version 1.0
 * @description: TODO
 * @date 2024/7/7 22:59
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UrlInfoServiceImpl  implements UrlInfoService {

    //jsoup连接网页超时时间
    private final int ConnectTimeOutMillis = 5000;

    @Override
    public String getUrlTitle(String url) {
        URL targetUrl = null;
        try {
            targetUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) targetUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(ConnectTimeOutMillis);
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Document document = Jsoup.connect(url).get();
                return document.title();
            }
        } catch (Exception e) {
            return "Error while fetching title.";
        }
        return "Error while fetching title.";
    }
}
