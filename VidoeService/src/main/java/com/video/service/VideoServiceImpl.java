package com.video.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.qcloud.vod.VodUploadClient;
import com.qcloud.vod.model.VodUploadRequest;
import com.qcloud.vod.model.VodUploadResponse;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.vod.v20180717.VodClient;
import com.tencentcloudapi.vod.v20180717.models.DeleteMediaRequest;
import com.tencentcloudapi.vod.v20180717.models.DeleteMediaResponse;
import com.video.client.StudentClient;
import com.video.client.TeacherClient;
import com.video.config.Yun;
import com.video.entity.*;
import com.video.mapper.CommentMapper;
import com.video.mapper.LikeMapper;
import com.video.mapper.MyCollectionsMapper;
import com.video.mapper.VideoMapper;
import com.video.service.service.VideoService;
import lombok.extern.slf4j.Slf4j;
import org.com.entity.Result;
import org.com.execption.MyException;
import org.com.mapper.Redis;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RefreshScope
@Slf4j
public class VideoServiceImpl implements VideoService {

    @Autowired
    private VideoMapper videoMapper;

    @Autowired
    private Redis redis;

    @Autowired
    private Gson gson;

    @Autowired
    private Yun yun;

    @Autowired
    private TeacherClient teacherClient;

    @Autowired
    private StudentClient studentClient;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private MyCollectionsMapper collectionsMapper;

    @Autowired
    private LikeMapper likeMapper;

    private ConcurrentHashMap<String, Process> processMap = new ConcurrentHashMap<>();

    @Override
    public void uploadVideo(MultipartFile file, String teacherid, String videoName, String videoText, String videoType, String duration) throws MyException {
        VodUploadClient client = new VodUploadClient(yun.getId(), yun.getKey());
        VodUploadRequest request = new VodUploadRequest();
        String pathVideo = "C:\\videoFile\\" + teacherid + "_" + videoName + ".mp4";
        try {
            file.transferTo(new File(pathVideo));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        request.setMediaFilePath(pathVideo);
        request.setCoverFilePath("img/log.png");
        Video video = new Video();
        try {
            VodUploadResponse response = client.upload("ap-guangzhou", request);
            log.info(response.getFileId());
            log.info(response.getMediaUrl());
            video.setVideoid(Long.parseLong(response.getFileId()));
            video.setUrl(response.getMediaUrl());
            video.setCoverUrl(response.getCoverUrl());
            video.setTeacherid(teacherid);
            video.setVideoName(videoName);
            video.setVideoText(videoText);
            video.setVideoType(videoType);
            video.setStartTime(LocalDateTime.now());
            video.setEndTime(LocalDateTime.now());
            int hours = Integer.valueOf(duration) / 3600;
            int remainingSeconds = Integer.valueOf(duration) % 3600;
            int minutes = remainingSeconds / 60;
            int seconds = remainingSeconds % 60;
            video.setDuration(LocalTime.parse(String.format("%02d:%02d:%02d", hours, minutes, seconds)));
            video.setStatus(1);
            video.setLikeNum(0L);
            video.setCollectNum(0L);
            video.setClassName("上传");
            videoMapper.insert(video);
//        videoMapper.insert(video.getId(), video.getTeacherid(), video.getUrl(), video.getVideoname());
            redis.setKey(String.valueOf(video.getVideoid()), gson.toJson(video));
        } catch (Exception e) {
            // 业务方进行异常处理
            throw new MyException("视频存储错误");
        } finally {
            File file1 = new File(pathVideo);
            file1.delete();
        }
        log.info("视频上传成功");
    }

    @Override
    public List<?> selectVideo(String videoName) throws MyException {
        List<Video> videos;
        if (redis.isExist(videoName)) {
            videos = (List<Video>) gson.fromJson(redis.getKey(videoName), ArrayList.class);
            return selectAfterUpdate(videos);
        }
        videos = (List<Video>) videoMapper.selectOne(
                Wrappers.<Video>lambdaQuery().eq(Video::getVideoName, videoName)
        );
//        videos = videoMapper.SelectVideo(videoName);
        if (videos != null) {
            redis.setKey(videoName, gson.toJson(videos));
            return selectAfterUpdate(videos);
        }
        throw new MyException("查询失败");
    }

    @Override
    public List<?> selectVideoByTeacher(String teacherid) throws MyException {
        ArrayList<Video> videos;
        IPage<Video> page = new Page<>(1, 12); // 当前页1，每页10条
        QueryWrapper<Video> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teacherid", teacherid);
        IPage<Video> userPage = videoMapper.selectPage(page, queryWrapper);
        videos = (ArrayList<Video>) userPage.getRecords(); // 获取当前页数据
        if (videos != null) {
            return selectAfterUpdate(videos);
        }
        throw new MyException("查询失败");
    }

    @Override
    public List<?> selectVideoPage(int pageSize, int pageNumber) throws MyException {
        ArrayList<Video> videos;
//        RowBounds rowBounds = new RowBounds((pageNumber - 1) * pageSize, pageSize);
//        videos = videoMapper.SelectVideoAll(rowBounds);
        IPage<Video> page = new Page<>(pageNumber, pageSize); // 当前页1，每页10条
        QueryWrapper<Video> queryWrapper = new QueryWrapper<>();
        IPage<Video> userPage = videoMapper.selectPage(page, queryWrapper);
        videos = (ArrayList<Video>) userPage.getRecords(); // 获取当前页数据
        if (videos != null) {
            return selectAfterUpdate(videos);
        }
        throw new MyException("查询失败");
    }

    @Override
    public void deleteVideo(String id) throws MyException {
        try {
            // 实例化一个认证对象，入参需要传入腾讯云账户secretId，secretKey,此处还需注意密钥对的保密
            // 密钥可前往https://console.cloud.tencent.com/cam/capi网站进行获取
            Credential cred = new Credential(yun.getId(), yun.getKey());
            // 实例化一个http选项，可选的，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("vod.tencentcloudapi.com");
            // 实例化一个client选项，可选的，没有特殊需求可以跳过
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            // 实例化要请求产品的client对象,clientProfile是可选的
            VodClient client = new VodClient(cred, "", clientProfile);
            // 实例化一个请求对象,每个接口都会对应一个request对象
            DeleteMediaRequest req = new DeleteMediaRequest();
            req.setFileId(id);
            // 返回的resp是一个DeleteMediaResponse的实例，与请求对象对应
            DeleteMediaResponse resp = client.DeleteMedia(req);
            // 输出json格式的字符串回包
            System.out.println(DeleteMediaResponse.toJsonString(resp));
        } catch (TencentCloudSDKException e) {
            throw new MyException("删除失败");
        }
        videoMapper.deleteById(id);
//        videoMapper.DeleteVideo(id);
    }

    @Override
    public void saveVideo(Video video) {
        videoMapper.insert(video);
//        videoMapper.insert(video.getId(), video.getTeacherid(), video.getUrl(), video.getVideoname());
        redis.setKey(String.valueOf(video.getVideoid()), gson.toJson(video));
    }

    @Override
    public void live(String teacherid) throws MyException {
        Process process = null;
        String ffmpeg = "ffmpeg -f v4l2 -input_format yuyv422 -video_size 640x480 -framerate 30 -i /dev/video0 -f alsa -i default -c:v libx264 -preset ultrafast -tune zerolatency -c:a aac -f flv rtmp://127.0.0.1:1935/hls/" + teacherid;
        ProcessBuilder pb = new ProcessBuilder("bash", "-c", ffmpeg);
        try {
            process = pb.start();
            if (processMap.containsKey(teacherid)) {
                throw new MyException("您已经开始直播，请勿重复点击");
            }
            processMap.put(teacherid, process);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (MyException e) {
            throw e;
        }
    }

    @Override
    public void stopLive(String teacherid) throws MyException {
        if (processMap.containsKey(teacherid)) {
            processMap.get(teacherid).destroy();
            processMap.remove(teacherid);
        } else {
            throw new MyException("您已结束直播或直播未开启无法关闭直播");
        }
    }

    @Override
    public VideoList selectVideoById(String id) throws MyException {
        if (redis.isExist(id)) {
            Video video = (Video) gson.fromJson(redis.getKey(id), Video.class);
            return afterUpdate(video);
        }
        Video video = videoMapper.selectById(id);
        redis.setKey(id, gson.toJson(video));
        return afterUpdate(video);
    }

    @Override
    public String getVideoDownloadUrl(String id) throws MyException {
        try {
            Video video = videoMapper.selectById(id);
            return video.getUrl() + "?download_name=" + video.getVideoName() + ".mp4";
        } catch (Exception e) {
            throw new MyException("获取下载地址失败");
        }

    }

    @Override
    public boolean update(String id, Video video) throws MyException {
        try {
            if (videoMapper.updateById(video) > 0) {
                redis.deleteKey(id);
                redis.setKey(id, gson.toJson(video));
                return true;
            }
        } catch (Exception e) {
            throw new MyException("更新失败");
        }
        return false;
    }

    @Override
    public void play(String videoid) throws MyException {
        String key = "play:" + videoid;
        if (redis.isExist(key)) {
            redis.incr(key);
        } else {
            Video video = videoMapper.selectOne(new QueryWrapper<Video>().eq("videoid", videoid));
            redis.setKey(key, String.valueOf(video.getViews() + 1), 60 * 24);
        }
    }

    @NotNull
    private static QueryWrapper getWrapper(String userid, String videoid, int userType) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("video_id", videoid);
        wrapper.eq("user_id", userid);
        wrapper.eq("user_type", userType);
        return wrapper;
    }

    @Override
    public long likeVideo(String userid, String videoid, int userType) throws MyException {
        String key = "like:" + videoid;
        if (redis.isExist(key)) {
            redis.incr(key);
        } else {
            Video video = videoMapper.selectOne(new QueryWrapper<Video>().eq("videoid", videoid));
            redis.setKey(key, String.valueOf(video.getLikeNum() + 1), 60 * 24);
        }

        QueryWrapper<Like> wrapper = getWrapper(userid, videoid, userType);
        if (likeMapper.selectOne(wrapper) == null) {
            Like like = new Like();
            like.setUserId(userid);
            like.setVideoId(Long.parseLong(videoid));
            like.setUserType(userType);
            likeMapper.insert(like);
        } else {
            throw new MyException("您已经点赞过了");
        }

        return Long.parseLong(redis.getKey(key));
    }

    @Override
    public long unlikeVideo(String userid, String videoid, int userType) throws MyException {
        String key = "like:" + videoid;
        long likeNum;
        if (redis.isExist(key)) {
            likeNum = Long.parseLong(redis.getKey(key));
        } else {
            Video video = videoMapper.selectOne(new QueryWrapper<Video>().eq("videoid", videoid));
            likeNum = video.getLikeNum();
        }
        if (likeNum > 0) {
            redis.decr(key);
        } else {
            throw new MyException("您已经取消点赞了");
        }
        QueryWrapper<Like> wrapper = getWrapper(userid, videoid, userType);
        likeMapper.delete(wrapper);
        return likeNum;
    }

    @Override
    public long collectVideo(String userid, String videoid, int userType) throws MyException {
        String key = "collect:" + videoid;
        if (redis.isExist(key)) {
            redis.incr(key);
        } else {
            Video video = videoMapper.selectOne(new QueryWrapper<Video>().eq("videoid", videoid));
            redis.setKey(key, String.valueOf(video.getCollectNum() + 1), 60 * 24);
        }
        QueryWrapper<MyCollections> wrapper = getWrapper(userid, videoid, userType);
        if (collectionsMapper.selectOne(wrapper) == null) {
            MyCollections myCollections = new MyCollections();
            myCollections.setUserId(userid);
            myCollections.setVideoId(Long.parseLong(videoid));
            myCollections.setUserType(userType);
            collectionsMapper.insert(myCollections);
        } else {
            throw new MyException("您已经收藏过了");
        }
        return Long.parseLong(redis.getKey(key));
    }

    @Override
    public long uncollectVideo(String userid, String videoid, int userType) throws MyException {
        String key = "collect:" + videoid;
        long collectNum;
        if (redis.isExist(key)) {
            collectNum = Long.parseLong(redis.getKey(key));
        } else {
            Video video = videoMapper.selectOne(new QueryWrapper<Video>().eq("videoid", videoid));
            collectNum = video.getCollectNum();
        }
        if (collectNum > 0) {
            redis.decr(key);
        } else {
            throw new MyException("您已经取消收藏了");
        }
        QueryWrapper<MyCollections> wrapper = getWrapper(userid, videoid, userType);
        collectionsMapper.delete(wrapper);
        return collectNum;
    }

    @Override
    public List<?> getComments(String videoid) throws MyException {
        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        wrapper.eq("videoid", videoid);
        List<Comment> comments = commentMapper.selectList(wrapper);
        return commentAfterUpdateList(comments);

    }

    private List<CommentList> commentAfterUpdateList(List<Comment> comments) {
        List<CommentList> commentLists = new ArrayList<>();
        for (Comment comment : comments) {
            CommentList comment1 = commentAfterUpdate(comment);
            commentLists.add(comment1);
        }
        return commentLists;
    }

    private CommentList commentAfterUpdate(Comment comment) {
        CommentList comment1 = new CommentList();
        BeanUtils.copyProperties(comment, comment1);
        if (comment.getUserType() == 1) {
            Result<Student> studentInfo = studentClient.getStudentInfo(comment1.getUserid());
            comment1.setAvatar(studentInfo.getData().getAvatar());
            comment1.setUserName(studentInfo.getData().getName());
        } else {
            Result<Teacher> teacherInfo = teacherClient.getTeacherInfo(comment1.getUserid());
            comment1.setAvatar(teacherInfo.getData().getAvatar());
            comment1.setUserName(teacherInfo.getData().getName());
        }
        return comment1;
    }

    @Override
    public void deleteComment(String commentid) throws MyException {
        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        wrapper.eq("commentid", commentid);
        commentMapper.delete(wrapper);
    }

    @Override
    public void addComment(String userid, String videoid, String content, int userType) throws MyException {
        Comment comment = new Comment();
        comment.setUserid(userid);
        comment.setVideoid(Long.parseLong(videoid));
        comment.setContent(content);
        comment.setUserType(userType);
        commentMapper.insert(comment);
    }

    @Override
    public void addReply(String userid, String videoid, String content, long parentId, int userType) throws MyException {
        Comment comment = new Comment();
        comment.setUserid(userid);
        comment.setVideoid(Long.parseLong(videoid));
        comment.setContent(content);
        comment.setUserType(userType);
        comment.setParentid(parentId);
        commentMapper.insert(comment);
    }

    @Override
    public Map<?, ?> getVideoStatus(String userid, String videoid, int userType) throws MyException {
        Map<String, Boolean> map = new HashMap<>();
        QueryWrapper<Like> likeWrapper = getWrapper(userid, videoid, userType);
        QueryWrapper<MyCollections> collectionsWrapper = getWrapper(userid, videoid, userType);
        MyCollections collections = collectionsMapper.selectOne(collectionsWrapper);
        if (collections == null) {
            map.put("isCollected", false);
        } else {
            map.put("isCollected", true);
        }
        Like like = likeMapper.selectOne(likeWrapper);
        if (like == null) {
            map.put("isLiked", false);
        } else {
            map.put("isLiked", true);
        }
        return map;
    }

    private List<VideoList> selectAfterUpdate(List<Video> data) {
        List<VideoList> videoLists = new ArrayList<>();
        for (Video video : data) {
            VideoList video1 = afterUpdate(video);
            videoLists.add(video1);
        }
        return videoLists;
    }

    @NotNull
    private VideoList afterUpdate(Video video) {
        String key = "play:" + video.getVideoid();
        if (redis.isExist(key)) {
            video.setViews(Long.parseLong(redis.getKey(key)));
        }
        String key1 = "like:" + video.getVideoid();
        if (redis.isExist(key1)) {
            video.setLikeNum(Long.parseLong(redis.getKey(key1)));
        }
        String key2 = "collect:" + video.getVideoid();
        if (redis.isExist(key2)) {
            video.setCollectNum(Long.parseLong(redis.getKey(key2)));
        }
        VideoList video1 = new VideoList();
        BeanUtils.copyProperties(video, video1);
        Result<Teacher> teacherInfo = teacherClient.getTeacherInfo(video.getTeacherid());
        video1.setTeacherName(teacherInfo.getData().getName());
        video1.setTeacherAvatar(teacherInfo.getData().getAvatar());
        return video1;
    }

}
