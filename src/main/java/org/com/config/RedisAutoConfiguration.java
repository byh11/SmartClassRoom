package org.com.config;


import org.com.mapper.Redis;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisAutoConfiguration {

    @Bean
    public Redis redis() {
        Redis redis = new Redis();
        return redis;
    }
}
