package com.video.contorller;

import com.video.entity.Video;
import com.video.service.VideoServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.com.entity.Result;
import org.com.execption.MyException;
import org.springframework.beans.factory.annotation.Autowired;
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

    private int test = 0;

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> test() {
        System.out.println("333333333333333333333");
        return Result.success("测试成功" + test++);
    }

    @RequestMapping(value = "/SaveVideo", method = RequestMethod.POST)
    @ResponseBody
    public Result<String> SaveVideo(@RequestBody Video video) {
        videoService.saveVideo(video);
//        System.out.println(video.toString());
        return Result.success("视频保存成功");
    }

    @RequestMapping(value = "/UploadVideo", method = RequestMethod.POST)
    @ResponseBody
    public Result<String> UploadVideo(MultipartFile file, String teacherid) {
        try {
            videoService.uploadVideo(file, teacherid);
        } catch (MyException e) {
            log.info(e.getMessage());
            return Result.error(e.getMessage());
        }
        return Result.success("视频保存成功");
    }

    @RequestMapping(value = "/SelectVideoAll", method = RequestMethod.POST)
    @ResponseBody
    public Result<ArrayList> SelectVideoAll(int pageSize, int pageNumber) {
        ArrayList<Video> videos = null;
        try {
            videos = videoService.selectVideoPage(pageSize, pageNumber);
        } catch (MyException e) {
            log.info(e.getMessage());
            return Result.error(e.getMessage());
        }
        return Result.success("查询成功", videos);
    }

    @RequestMapping(value = "/SelectVideo", method = RequestMethod.POST)
    @ResponseBody
    public Result<ArrayList> SelectVideo(String videoName) {
        ArrayList<Video> videos = null;
        try {
            videos = videoService.selectVideo(videoName);
        } catch (MyException e) {
            log.info(e.getMessage());
            return Result.error(e.getMessage());
        }
        return Result.success("查询成功", videos);
    }

    @RequestMapping(value = "/DeleteVideo", method = RequestMethod.POST)
    @ResponseBody
    public Result<String> DeleteVideo(String id) {
        try {
            videoService.deleteVideo(id);
        } catch (MyException e) {
            log.info(e.getMessage());
            return Result.error(e.getMessage());
        }
        return Result.success("删除成功");
    }

    @RequestMapping(value = "/DownLoadVideo", method = RequestMethod.POST)
    @ResponseBody
    public Result<String> DownLoadVideo(String id) {
        videoService.downLoadVideo(id);
        return Result.success("下载成功");
    }

    @RequestMapping(value = "/live", method = RequestMethod.POST)
    @ResponseBody
    public Result<String> Live(String teacherid) {
        try {
            videoService.live(teacherid);
        } catch (MyException e) {
            return Result.error(e.getMessage());
        }
        return Result.success("开始直播");
    }

    @RequestMapping(value = "/stoplive", method = RequestMethod.POST)
    @ResponseBody
    public Result<String> stoplive(String teacherid) {
        try {
            videoService.stopLive(teacherid);
        } catch (MyException e) {
            return Result.error(e.getMessage());
        }
        return Result.success("停止直播");
    }
}
