package com.video.contorller;

import com.video.entity.Video;
import com.video.service.VideoServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.com.entity.Result;
import org.com.execption.MyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

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
    public Result<String> saveVideo(@RequestBody Video video) {
        videoService.saveVideo(video);
//        System.out.println(video.toString());
        return Result.success("视频保存成功");
    }

    @RequestMapping(value = "/{teacherid}/upload", method = RequestMethod.POST)
    @ResponseBody
    public Result<String> uploadVideo(@PathVariable String teacherid,
                                      @RequestParam("file") MultipartFile file,
                                      @RequestParam("videoName") String videoName,
                                      @RequestParam("videoText") String videoText,
                                      @RequestParam("videoType") String videoType,
                                      @RequestParam("duration") String duration) {
        try {
            videoService.uploadVideo(file, teacherid, videoName, videoText, videoType, duration);
        } catch (MyException e) {
            log.info(e.getMessage());
            return Result.error(e.getMessage());
        }
        return Result.success("视频保存成功");
    }

    @RequestMapping(value = "/SelectVideoAll", method = RequestMethod.GET)
    @ResponseBody
    public Result<List> selectVideoAll(@RequestParam("pageNumber") int pageNumber, @RequestParam("pageSize") int pageSize) {
        List<?> videos = null;
        try {
            videos = videoService.selectVideoPage(pageSize, pageNumber);
        } catch (MyException e) {
            log.info(e.getMessage());
            return Result.error(e.getMessage());
        }
        return Result.success("查询成功", videos);
    }

    @RequestMapping(value = "/SelectVideoByName", method = RequestMethod.POST)
    @ResponseBody
    public Result<List> selectVideo(String videoName) {
        List<?> videos = null;
        try {
            videos = videoService.selectVideo(videoName);
        } catch (MyException e) {
            log.info(e.getMessage());
            return Result.error(e.getMessage());
        }
        return Result.success("查询成功", videos);
    }

    @RequestMapping(value = "/SelectVideoByTeacher/{teacherid}", method = RequestMethod.GET)
    @ResponseBody
    public Result<?> SelectVideoByTeacher(@PathVariable("teacherid") String teacherid) {
        List<?> videos = null;
        try {
            videos = videoService.selectVideoByTeacher(teacherid);
        } catch (MyException e) {
            log.info(e.getMessage());
            return Result.error(e.getMessage());
        }
        return Result.success("查询成功", videos);
    }

    @RequestMapping(value = "/DeleteVideo", method = RequestMethod.POST)
    @ResponseBody
    public Result<String> deleteVideo(String id) {
        try {
            videoService.deleteVideo(id);
        } catch (MyException e) {
            log.info(e.getMessage());
            return Result.error(e.getMessage());
        }
        return Result.success("删除成功");
    }


    @RequestMapping(value = "/live", method = RequestMethod.POST)
    @ResponseBody
    public Result<String> live(String teacherid) {
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

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Result<?> getVideoDetail(@PathVariable String id) throws MyException {
        try {
            Video video = videoService.selectVideoById(id);
            return Result.success(video);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @RequestMapping(value = "/{id}/download", method = RequestMethod.GET)
    @ResponseBody
    public Result<?> getVideoDownloadUrl(@PathVariable String id) throws MyException {
        try {
            String downloadUrl = videoService.getVideoDownloadUrl(id);
            return Result.success("获取成功", downloadUrl);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> updateVideo(@PathVariable String id, @RequestBody Video requestBody) throws MyException {
        try {
            boolean update = videoService.update(id, requestBody);
            if (update) {
                return Result.success("更新成功");
            } else {
                return Result.error("更新失败");
            }
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @RequestMapping(value = "/play/{videoid}", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> play(@PathVariable String videoid) throws MyException {
        try {
            videoService.play(videoid);
            return Result.success("播放成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @RequestMapping(value = "/{userid}/like/{videoid}/{userType}", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> likeVideo(@PathVariable String userid, @PathVariable String videoid, @PathVariable int userType) throws MyException {
        try {
            long l = videoService.likeVideo(userid, videoid, userType);
            return Result.success("点赞成功", l);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @RequestMapping(value = "/{userid}/unlike/{videoid}/{userType}", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> unlikeVideo(@PathVariable String userid, @PathVariable String videoid, @PathVariable int userType) throws MyException {
        try {
            long l = videoService.unlikeVideo(userid, videoid, userType);
            return Result.success("取消点赞成功", l);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @RequestMapping(value = "/{userid}/collections/{videoid}/{userType}", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> collectVideo(@PathVariable String userid, @PathVariable String videoid, @PathVariable int userType) throws MyException {
        try {
            long l = videoService.collectVideo(userid, videoid, userType);
            return Result.success("收藏成功", l);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @RequestMapping(value = "/{userid}/uncollections/{videoid}/{userType}", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> uncollectVideo(@PathVariable String userid, @PathVariable String videoid, @PathVariable int userType) throws MyException {
        try {
            long l = videoService.uncollectVideo(userid, videoid, userType);
            return Result.success("取消收藏成功", l);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @RequestMapping(value = "/{videoid}/comments", method = RequestMethod.GET)
    @ResponseBody
    public Result<?> getComments(@PathVariable String videoid) throws MyException {
        try {
            return Result.success("评论获取成功", videoService.getComments(videoid));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }


    @RequestMapping(value = "/{userid}/comments/{videoid}/{userType}", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> addComment(@PathVariable String userid, @PathVariable String videoid, @RequestParam("content") String content, @PathVariable int userType) throws MyException {
        try {
            videoService.addComment(userid, videoid, content, userType);
            return Result.success("评论成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @RequestMapping(value = "/comments/del/{commentid}", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> deleteComment(@PathVariable String commentid) throws MyException {
        try {
            videoService.deleteComment(commentid);
            return Result.success("删除评论成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @RequestMapping(value = "/{userid}/reply/{videoid}/{userType}", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> addReply(@PathVariable String userid, @PathVariable String videoid, @RequestParam("content") String content, @RequestParam("parentId") String parentId, @PathVariable int userType) throws MyException {
        try {
            videoService.addReply(userid, videoid, content, Long.parseLong(parentId), userType);
            return Result.success("评论成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @RequestMapping(value = "/{userid}/status/{videoid}/{userType}", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> getVideoStatus(@PathVariable String userid, @PathVariable String videoid, @PathVariable int userType) throws MyException {
        try {
            Map<?, ?> videoStatus = videoService.getVideoStatus(userid, videoid, userType);
            return Result.success("获取成功", videoStatus);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @RequestMapping(value = "/getVideoTopByField", method = RequestMethod.GET)
    @ResponseBody
    public Result<List> getVideoTopByField(@RequestParam("pageNumber") int pageNumber, @RequestParam("pageSize") int pageSize, @RequestParam("field") String field, @RequestParam("order") String order) {
        List<?> videos = null;
        try {
            videos = videoService.getVideoTopByField(pageNumber, pageSize, field, order);
        } catch (MyException e) {
            log.info(e.getMessage());
            return Result.error(e.getMessage());
        }
        return Result.success("查询成功", videos);
    }

    @RequestMapping(value = "/getVideoByName", method = RequestMethod.GET)
    @ResponseBody
    public Result<List> getVideoByName(@RequestParam("pageNumber") int pageNumber, @RequestParam("pageSize") int pageSize, @RequestParam("name") String name) {
        List<?> videos = null;
        try {
            videos = videoService.getVideoByName(pageNumber, pageSize, name);
        } catch (MyException e) {
            log.info(e.getMessage());
            return Result.error(e.getMessage());
        }
        return Result.success("查询成功", videos);
    }

    @RequestMapping(value = "/getVideoByType", method = RequestMethod.GET)
    @ResponseBody
    public Result<List> getVideoByType(@RequestParam("pageNumber") int pageNumber, @RequestParam("pageSize") int pageSize, @RequestParam("type") String type) {
        List<?> videos = null;
        try {
            videos = videoService.getVideoByType(pageNumber, pageSize, type);
        } catch (MyException e) {
            log.info(e.getMessage());
            return Result.error(e.getMessage());
        }
        return Result.success("查询成功", videos);
    }

    @RequestMapping(value = "/liked", method = RequestMethod.GET)
    @ResponseBody
    public Result<List> getLiked(@RequestParam String userid, @RequestParam int userType) {
        List<?> videos = null;
        try {
            videos = videoService.getLiked(userid, userType);
        } catch (MyException e) {
            log.info(e.getMessage());
            return Result.error(e.getMessage());
        }
        return Result.success("查询成功", videos);
    }

    @RequestMapping(value = "/collected", method = RequestMethod.GET)
    @ResponseBody
    public Result<List> getCollected(@RequestParam String userid, @RequestParam int userType) {
        List<?> videos = null;
        try {
            videos = videoService.getCollected(userid, userType);
        } catch (MyException e) {
            log.info(e.getMessage());
            return Result.error(e.getMessage());
        }
        return Result.success("查询成功", videos);
    }



}
