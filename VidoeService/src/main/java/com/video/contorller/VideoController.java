package com.video.contorller;

import com.video.config.Yun;
import com.video.entity.Video;
import com.video.service.VideoServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.com.entity.Result;
import org.com.execption.MyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

@Controller
@RequestMapping("/video")
@RefreshScope
@Slf4j
public class VideoController {

    @Autowired
    private VideoServiceImpl videoService;

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> test() {
        System.out.println("333333333333333333333");
        return Result.success("测试成功");
    }

    @RequestMapping(value = "/SaveVideo")
    @ResponseBody
    public Result<String> SaveVideo(@RequestBody Video video) {
        videoService.SaveVideo(video);
//        System.out.println(video.toString());
        return Result.success("视频保存成功");
    }

    @RequestMapping(value = "/UploadVideo", method = RequestMethod.POST)
    @ResponseBody
    public Result<String> UploadVideo(MultipartFile file, String teacherid) {
        try {
            videoService.UploadVideo(file,teacherid);
        } catch (MyException e) {
            log.info(e.getMessage());
            return Result.error(e.getMessage());
        }
        return Result.success("视频保存成功");
    }

    @RequestMapping(value = "/SelectVideo", method = RequestMethod.POST)
    @ResponseBody
    public Result<ArrayList> SelectVideo(String videoName) {
        ArrayList<Video> videos = null;
        try {
            videos = videoService.SelectVideo(videoName);
        } catch (MyException e) {
            log.info(e.getMessage());
            return Result.error(e.getMessage());
        }
        return Result.success("查询成功",videos);
    }

    @RequestMapping(value = "/DeleteVideo", method = RequestMethod.POST)
    @ResponseBody
    public Result<String> DeleteVideo(String id) {
        try {
            videoService.DeleteVideo(id);
        } catch (MyException e) {
            log.info(e.getMessage());
            return Result.error(e.getMessage());
        }
        return Result.success("删除成功");
    }

    @RequestMapping(value = "/DownLoadVideo", method = RequestMethod.POST)
    @ResponseBody
    public Result<String> DownLoadVideo(String id) {
        videoService.DownLoadVideo(id);
        return Result.success("下载成功");
    }
}
