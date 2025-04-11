package com.fox.client;

import com.fox.entity.Result;
import com.fox.entity.Video;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@FeignClient(name = "video-service", url = "http://10.0.220.99:12000") // 根据实际服务地址调整
public interface VideoClient {

    // 测试接口
    @GetMapping("/video/test")
    Result<String> test();

    // 保存视频
    @PostMapping("/video/SaveVideo")
    Result<String> saveVideo(@RequestBody Video video);

    // 上传视频
    @PostMapping("/video/{teacherid}/upload")
    Result<String> uploadVideo(
            @PathVariable("teacherid") String teacherid,
            @RequestParam("file") MultipartFile file,
            @RequestParam("videoName") String videoName,
            @RequestParam("videoText") String videoText,
            @RequestParam("videoType") String videoType,
            @RequestParam("duration") String duration
    );

    // 分页查询视频
    @GetMapping("/video/SelectVideoAll")
    Result<List<?>> selectVideoAll(
            @RequestParam("pageNumber") int pageNumber,
            @RequestParam("pageSize") int pageSize
    );

    // 按名称查询视频
    @PostMapping("/video/SelectVideoByName")
    Result<List<?>> selectVideoByName(@RequestParam("videoName") String videoName);

    // 按教师ID查询视频
    @GetMapping("/video/SelectVideoByTeacher/{teacherid}")
    Result<?> selectVideoByTeacher(@PathVariable("teacherid") String teacherid);

    // 删除视频
    @PostMapping("/video/DeleteVideo")
    Result<String> deleteVideo(@RequestParam("id") String id);

    // 开始直播
    @PostMapping("/video/live")
    Result<String> live(@RequestParam("teacherid") String teacherid);

    // 停止直播
    @PostMapping("/video/stoplive")
    Result<String> stoplive(@RequestParam("teacherid") String teacherid);

    // 获取视频详情
    @GetMapping("/video/{id}")
    Result<?> getVideoDetail(@PathVariable("id") String id);

    // 获取视频下载链接
    @GetMapping("/video/{id}/download")
    Result<?> getVideoDownloadUrl(@PathVariable("id") String id);

    // 更新视频信息
    @PostMapping("/video/update/{id}")
    Result<?> updateVideo(
            @PathVariable("id") String id,
            @RequestBody Video requestBody
    );

    // 播放视频
    @PostMapping("/video/play/{videoid}")
    Result<?> play(@PathVariable("videoid") String videoid);

    // 点赞视频
    @PostMapping("/video/{userid}/like/{videoid}/{userType}")
    Result<?> likeVideo(
            @PathVariable("userid") String userid,
            @PathVariable("videoid") String videoid,
            @PathVariable("userType") int userType
    );

    // 取消点赞
    @PostMapping("/video/{userid}/unlike/{videoid}/{userType}")
    Result<?> unlikeVideo(
            @PathVariable("userid") String userid,
            @PathVariable("videoid") String videoid,
            @PathVariable("userType") int userType
    );

    // 收藏视频
    @PostMapping("/video/{userid}/collections/{videoid}/{userType}")
    Result<?> collectVideo(
            @PathVariable("userid") String userid,
            @PathVariable("videoid") String videoid,
            @PathVariable("userType") int userType
    );

    // 取消收藏
    @PostMapping("/video/{userid}/uncollections/{videoid}/{userType}")
    Result<?> uncollectVideo(
            @PathVariable("userid") String userid,
            @PathVariable("videoid") String videoid,
            @PathVariable("userType") int userType
    );

    // 获取评论列表
    @GetMapping("/video/{videoid}/comments")
    Result<?> getComments(@PathVariable("videoid") String videoid);

    // 添加评论
    @PostMapping("/video/{userid}/comments/{videoid}/{userType}")
    Result<?> addComment(
            @PathVariable("userid") String userid,
            @PathVariable("videoid") String videoid,
            @RequestParam("content") String content,
            @PathVariable("userType") int userType
    );

    // 删除评论
    @PostMapping("/video/comments/del/{commentid}")
    Result<?> deleteComment(@PathVariable("commentid") String commentid);

    // 添加回复
    @PostMapping("/video/{userid}/reply/{videoid}/{userType}")
    Result<?> addReply(
            @PathVariable("userid") String userid,
            @PathVariable("videoid") String videoid,
            @RequestParam("content") String content,
            @RequestParam("parentId") String parentId,
            @PathVariable("userType") int userType
    );
}