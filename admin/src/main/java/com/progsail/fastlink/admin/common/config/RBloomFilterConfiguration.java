package com.progsail.fastlink.admin.common.config;



import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yangfan
 * @version 1.0
 * @description: 布隆过滤器配置
 * @date 2024/2/14 10:47
 */
@Configuration(value = "rBloomFilterConfigurationByAdmin")
public class RBloomFilterConfiguration {

    /**
     * 防止用户注册查询数据库的布隆过滤器
     */
    @Bean
    public RBloomFilter<String> userRegisterCachePenetrationBloomFilter(RedissonClient redissonClient) {
        RBloomFilter<String> cachePenetrationBloomFilter = redissonClient.getBloomFilter("userRegisterCachePenetrationBloomFilter");
        cachePenetrationBloomFilter.tryInit(1000000L, 0.001);
        return cachePenetrationBloomFilter;
    }
}
