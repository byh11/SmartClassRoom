package com.video.service;

import com.video.entity.Video;
import com.video.mapper.VideoMapper;
import lombok.extern.slf4j.Slf4j;
import org.com.mapper.Redis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@Slf4j
public class AutoTaskService {

    @Autowired
    Redis redis;

    @Autowired
    VideoMapper videoMapper;

    @Scheduled(cron = "0 */30 * * * ?")
    public void updateViews() {
        log.info("同步播放数据开始");
        Set<String> playKeysAndValues = redis.getKeysStartingWithLike("play:*");
        for (String key : playKeysAndValues) {
            String value = redis.getKey(key);
            String videoId = key.split(":")[1];
            if (value != null) {
                Video video = videoMapper.selectById(videoId);
                video.setViews(Long.valueOf(value));
                videoMapper.updateById(video);
            }
        }
        log.info("同步播放数据结束");
    }

    @Scheduled(cron = "0 */30 * * * ?")
    public void updateLikeNum() {
        log.info("同步赞数据开始");
        Set<String> likeKeysAndValues = redis.getKeysStartingWithLike("like:*");
        for (String key : likeKeysAndValues) {
            String value = redis.getKey(key);
            String videoId = key.split(":")[1];
            if (value != null) {
                Video video = videoMapper.selectById(videoId);
                video.setLikeNum(Long.valueOf(value));
                videoMapper.updateById(video);
            }
        }
        log.info("同步赞数据结束");
    }

    @Scheduled(cron = "0 */30 * * * ?")
    public void updateCollectNum() {
        log.info("同步收藏数据开始");
        Set<String> collectKeysAndValues = redis.getKeysStartingWithLike("collect:*");
        for (String key : collectKeysAndValues) {
            String value = redis.getKey(key);
            String videoId = key.split(":")[1];
            if (value != null) {
                Video video = videoMapper.selectById(videoId);
                video.setCollectNum(Long.valueOf(value));
                videoMapper.updateById(video);
            }
        }
        log.info("同步收藏数据结束");
    }


}
