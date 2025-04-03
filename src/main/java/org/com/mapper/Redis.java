package org.com.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class Redis {
    @Autowired
    @Qualifier("myRedisTemplate")
    private RedisTemplate redisTemplate;

    public boolean isExist(String key) {
        // 使用RedisTemplate的hasKey方法判断指定key是否存在
        return redisTemplate.hasKey(key);
    }

    public void setKey(String key, String value) {
        // 使用RedisTemplate的opsForValue方法设置指定key的值
        redisTemplate.opsForValue().set(key, value, 60 * 24, TimeUnit.MINUTES);
//
//        // 设置key的过期时间为15分钟
//        redisTemplate.expire(key, 60, TimeUnit.MINUTES);

    }

    public void setKey(String key, String value, int expireTime) {
        // 使用RedisTemplate的opsForValue方法设置指定key的值
        redisTemplate.opsForValue().set(key, value, expireTime, TimeUnit.MINUTES);
//
//        // 设置key的过期时间为expireTime秒
//        redisTemplate.expire(key, expireTime, TimeUnit.MINUTES);
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

    public void incr(String key) {
        String v = redisTemplate.opsForValue().get(key).toString();
        redisTemplate.opsForValue().set(key, String.valueOf(Integer.parseInt(v) + 1));
    }

    public void decr(String key) {
        String v = redisTemplate.opsForValue().get(key).toString();
        redisTemplate.opsForValue().set(key, String.valueOf(Integer.parseInt(v) - 1));
    }

    public Set<String> getKeysStartingWithLike(String pattern) {
        // 使用通配符 * 匹配前缀为 "like:" 的所有 key
        return redisTemplate.keys(pattern);
    }

}

