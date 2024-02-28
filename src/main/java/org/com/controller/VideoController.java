package org.com.controller;

import org.com.entity.Result;
import org.com.entity.Video;
import org.com.execption.MyException;
import org.com.service.VideoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;

@Controller
@RequestMapping("/video")
public class VideoController {

    @Autowired
    private VideoServiceImpl videoService;


    @RequestMapping(value = "/UploadVideo", method = RequestMethod.POST)
    @ResponseBody
    public Result<String> UploadVideo(MultipartFile file, String teacherid) throws MyException {
        videoService.UploadVideo(file,teacherid);
        return Result.success("视频保存成功");
    }

    @RequestMapping(value = "/SelectVideo", method = RequestMethod.POST)
    @ResponseBody
    public Result<ArrayList> SelectVideo(String videoName) throws MyException {
        ArrayList<Video> videos = videoService.SelectVideo(videoName);
        return Result.success("查询成功",videos);
    }

    @RequestMapping(value = "/DeleteVideo", method = RequestMethod.POST)
    @ResponseBody
    public Result<String> DeleteVideo(String id) throws MyException {
        videoService.DeleteVideo(id);
        return Result.success("删除成功");
    }

    @RequestMapping(value = "/DownLoadVideo", method = RequestMethod.POST)
    @ResponseBody
    public Result<String> DownLoadVideo(String id) throws MyException {
        videoService.DownLoadVideo(id);
        return Result.success("下载成功");
    }
}
