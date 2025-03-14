package com.teacher.client;


import com.teacher.entity.Video;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;


@FeignClient(name = "video-service")
public interface VideoClient {

    @PostMapping("/video/SaveVideo")
    @ResponseBody
    void SaveVideo(@RequestBody Video video);
}
