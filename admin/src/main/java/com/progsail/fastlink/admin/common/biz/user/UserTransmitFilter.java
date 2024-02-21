package com.progsail.fastlink.admin.common.biz.user;

import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Lists;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.IOException;
import java.util.List;

import static com.progsail.fastlink.admin.common.constant.RedisCacheConstant.USER_LOGIN_KEY;

/**
 * @author yangfan
 * @version 1.0
 * @description: 用户消息传输过滤器
 * @date 2024/2/21 11:09
 */
@RequiredArgsConstructor
public class UserTransmitFilter implements Filter {

    private final StringRedisTemplate stringRedisTemplate;

    public static final List<String> IGNORE_URL = Lists.newArrayList(
            "/api/fast-link/v1/user/register",
            "/api/fast-link/v1/user/has-username",
            "/api/fast-link/v1/user/login",
            "/api/fast-link/admin/v1/user/check-login",
            "/api/fast-link/admin/v1/user/logout"
    );
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String requestUrl = httpServletRequest.getRequestURI();
        if(!IGNORE_URL.contains(requestUrl)){
            String username = httpServletRequest.getHeader("username");
            String token = httpServletRequest.getHeader("token");
            Object userInfoJsonStr = stringRedisTemplate.opsForHash().get(USER_LOGIN_KEY+username, token);
            if(userInfoJsonStr != null) {
                UserInfoDTO userInfoDTO = JSON.parseObject(userInfoJsonStr.toString(), UserInfoDTO.class);
                UserContext.setUser(userInfoDTO);
            }
        }

        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            UserContext.removeUser();
        }
    }
}
