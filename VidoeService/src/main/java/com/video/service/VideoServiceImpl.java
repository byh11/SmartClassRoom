package com.video.service;

import com.video.config.Yun;
import com.video.entity.Video;
import com.google.gson.Gson;

import com.video.mapper.Redis;
import com.video.mapper.VideoMapper;
import com.qcloud.vod.VodUploadClient;
import com.qcloud.vod.model.VodUploadRequest;
import com.qcloud.vod.model.VodUploadResponse;
import com.video.service.service.VideoService;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.vod.v20180717.VodClient;
import com.tencentcloudapi.vod.v20180717.models.DeleteMediaRequest;
import com.tencentcloudapi.vod.v20180717.models.DeleteMediaResponse;
import lombok.extern.slf4j.Slf4j;
import org.com.execption.MyException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.tencentcloudapi.common.Credential;
import java.io.File;

import java.io.IOException;

import java.util.ArrayList;

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

    @Override
    public void UploadVideo(MultipartFile file, String teacherid) throws MyException {
        VodUploadClient client = new VodUploadClient(yun.getId(), yun.getKey());
        VodUploadRequest request = new VodUploadRequest();
        try {
            file.transferTo(new java.io.File("/opt/SmartClassRoom/11/"+file.getOriginalFilename()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        request.setMediaFilePath("/opt/SmartClassRoom/11/"+file.getOriginalFilename());
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
        videoMapper.insert(video.getId(), video.getTeacherid(), video.getUrl(), video.getVideoname());
        redis.registerSet(String.valueOf(video.getId()), gson.toJson(video));
        File file1 = new File("/opt/SmartClassRoom/11/"+file.getOriginalFilename());
        file1.delete();
    }

    @Override
    public ArrayList<Video> SelectVideo(String videoName) throws MyException {
        ArrayList<Video> videos;
        if (redis.isExist(videoName)){
            videos = (ArrayList<Video>) gson.fromJson(redis.getKey(videoName), ArrayList.class);
            return videos;
        }
        videos = videoMapper.SelectVideo(videoName);
        if (videos!=null){
            redis.registerSet(videoName,gson.toJson(videos));
            return videos;
        }
        throw new MyException("查询失败");
    }

    @Override
    public void DeleteVideo(String id) throws MyException {
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
        videoMapper.DeleteVideo(id);
    }

    @Override
    public void DownLoadVideo(String id) {


    }
}
