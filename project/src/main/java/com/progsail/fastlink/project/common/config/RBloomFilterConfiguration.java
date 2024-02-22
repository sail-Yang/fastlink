package com.progsail.fastlink.project.common.config;



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
@Configuration(value = "rBloomFilterConfigurationByProject")
public class RBloomFilterConfiguration {

    /**
     * 防止短链接创建频繁查询数据库的布隆过滤器
     */
    @Bean
    public RBloomFilter<String> shortLinkCreateCachePenetrationBloomFilter(RedissonClient redissonClient) {
        RBloomFilter<String> cachePenetrationBloomFilter = redissonClient.getBloomFilter("shortLinkCreateCachePenetrationBloomFilter");
        cachePenetrationBloomFilter.tryInit(1000000L, 0.001);
        return cachePenetrationBloomFilter;
    }
}
