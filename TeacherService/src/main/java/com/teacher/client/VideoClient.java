package com.teacher.client;


import com.teacher.entity.Video;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;


@FeignClient(name = "video-service")
public interface VideoClient {

    @PostMapping("/video/SaveVideo")
    @ResponseBody
    void SaveVideo(@RequestBody Video video);
}
