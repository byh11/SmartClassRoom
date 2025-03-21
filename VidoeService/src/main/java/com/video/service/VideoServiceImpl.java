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
import com.video.config.Yun;
import com.video.entity.Video;
import com.video.mapper.VideoMapper;
import com.video.service.service.VideoService;
import lombok.extern.slf4j.Slf4j;
import org.com.execption.MyException;
import org.com.mapper.Redis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

    private ConcurrentHashMap<String, Process> processMap = new ConcurrentHashMap<>();

    @Override
    public void uploadVideo(MultipartFile file, String teacherid) throws MyException {
        VodUploadClient client = new VodUploadClient(yun.getId(), yun.getKey());
        VodUploadRequest request = new VodUploadRequest();
        try {
            file.transferTo(new java.io.File("/opt/SmartClassRoom/11/" + file.getOriginalFilename()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        request.setMediaFilePath("/opt/SmartClassRoom/11/" + file.getOriginalFilename());
        Video video = new Video();
        try {
            VodUploadResponse response = client.upload("ap-guangzhou", request);
            System.out.println(response.getFileId());
            System.out.println(response.getMediaUrl());
            video.setId(Long.parseLong(response.getFileId()));
            video.setUrl(response.getMediaUrl());
            System.out.println(video.getUrl().length());
            video.setVideoname(file.getOriginalFilename());
            video.setTeacherid(teacherid);
        } catch (Exception e) {
            // 业务方进行异常处理
            throw new MyException("视频存储错误");
        }
        videoMapper.insert(video);
//        videoMapper.insert(video.getId(), video.getTeacherid(), video.getUrl(), video.getVideoname());
        redis.setKey(String.valueOf(video.getId()), gson.toJson(video));
        File file1 = new File("/opt/SmartClassRoom/11/" + file.getOriginalFilename());
        file1.delete();
    }

    @Override
    public List<Video> selectVideo(String videoName) throws MyException {
        List<Video> videos;
        if (redis.isExist(videoName)) {
            videos = (List<Video>) gson.fromJson(redis.getKey(videoName), ArrayList.class);
            return videos;
        }
        videos = (List<Video>) videoMapper.selectOne(
                Wrappers.<Video>lambdaQuery().eq(Video::getVideoname, videoName)
        );
//        videos = videoMapper.SelectVideo(videoName);
        if (videos != null) {
            redis.setKey(videoName, gson.toJson(videos));
            return videos;
        }
        throw new MyException("查询失败");
    }

    @Override
    public List<Video> selectVideoPage(int pageSize, int pageNumber) throws MyException {

        ArrayList<Video> videos;
//        RowBounds rowBounds = new RowBounds((pageNumber - 1) * pageSize, pageSize);
//        videos = videoMapper.SelectVideoAll(rowBounds);
        IPage<Video> page = new Page<>(1, 12); // 当前页1，每页10条
        QueryWrapper<Video> queryWrapper = new QueryWrapper<>();
        IPage<Video> userPage = videoMapper.selectPage(page, queryWrapper);
        videos = (ArrayList<Video>) userPage.getRecords(); // 获取当前页数据
        if (videos != null) {
            return videos;
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
    public void downLoadVideo(String id) {


    }

    @Override
    public void saveVideo(Video video) {
        videoMapper.insert(video);
//        videoMapper.insert(video.getId(), video.getTeacherid(), video.getUrl(), video.getVideoname());
        redis.setKey(String.valueOf(video.getId()), gson.toJson(video));
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


}
