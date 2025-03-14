package com.video.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class Redis {
    @Autowired
    private RedisTemplate redisTemplate;

    public boolean isExist(String key) {
        // 使用RedisTemplate的hasKey方法判断指定key是否存在
        return redisTemplate.hasKey(key);
    }

    public void registerSet(String key, String value) {
        // 使用RedisTemplate的opsForValue方法设置指定key的值
        redisTemplate.opsForValue().set(key, value);

        // 设置key的过期时间为15分钟
        redisTemplate.expire(key, 60, TimeUnit.MINUTES);

    }

    public String getKey(String username) {
        if (redisTemplate.hasKey(username)) {
            return redisTemplate.opsForValue().get(username).toString();
        }
        return "";
    }

    public void deleteKey(String username) {
        redisTemplate.delete(username);
    }
}
