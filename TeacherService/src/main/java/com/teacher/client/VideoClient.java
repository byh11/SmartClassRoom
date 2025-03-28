package com.teacher.client;


import com.teacher.entity.Video;
import org.com.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;


@FeignClient(name = "video-service")
public interface VideoClient {

    @PostMapping("/video/SaveVideo")
    @ResponseBody
    void SaveVideo(@RequestBody Video video);

    @PostMapping("/video/SelectVideoByTeacher")
    @ResponseBody
    Result<List> SelectVideoByTeacher(@RequestParam("teacherid") String teacherid);
}
