package com.progsail.fastlink.project.util;

import jakarta.servlet.http.HttpServletRequest;

import static com.progsail.fastlink.project.common.constant.ShortLinkConstant.UNKNOWN;

/**
 * @author yangfan
 * @version 1.0
 * @description: 统计信息工具类
 * @date 2024/8/6 11:29
 */
public class AccessUtil {
    private static final String LOCAL_IP="127.0.0.1";

    public static String getIpAddr(HttpServletRequest request){
        if(request == null){
            return UNKNOWN;
        }
        String ip = request.getHeader("x-forwarded-for");
        if(ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)){
            ip = request.getHeader("Proxy-Client-IP");
        }
        if(ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)){
            ip = request.getHeader("X-Forwarded-For");
        }
        if(ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)){
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if(ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)){
            ip = request.getHeader("X-Real-IP");
        }
        if(ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)){
            ip = request.getRemoteAddr();
        }
        return "0.0.0.0".equals(ip) ? LOCAL_IP : ip;
    }
}
