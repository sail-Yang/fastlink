package com.progsail.fastlink.admin.common.config;

import com.progsail.fastlink.admin.common.filter.ExceptionFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yangfan
 * @version 1.0
 * @description: 将异常传输给controller
 * @date 2024/2/23 11:07
 */
@Configuration
public class GlobalExceptionFilterConfiguration {

    @Bean
    public FilterRegistrationBean<ExceptionFilter> exceptionFilterRegistration() {
        FilterRegistrationBean<ExceptionFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new ExceptionFilter());
        registration.setName("ExceptionFilter");
        //此处尽量小，要比其他Filter靠前
        registration.setOrder(-1);
        return registration;
    }
}
