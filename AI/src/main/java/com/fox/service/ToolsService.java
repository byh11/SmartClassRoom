package com.fox.service;

import com.fox.client.StudentClient;
import com.fox.client.TeacherClient;
import com.fox.client.VideoClient;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ToolsService {

    @Autowired
    private TeacherClient teacherClient;

    @Autowired
    private StudentClient studentClient;

    @Autowired
    private VideoClient videoClient;

    private static List<String> getVideoString(ArrayList<?> data) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            Map<String, String> map = (Map<String, String>) data.get(i);
            String info = String.format("视频url：%s，视频名称：%s，视频播放数：%s，视频收藏数：%s，视频点赞数：%s，视频简介：%s，视频时长：%s",
                    map.get("videoid"), map.get("videoName"), map.get("views"), map.get("collectNum"), map.get("likeNum"), map.get("videoText"), map.get("duration"));
            list.add(info);
        }
        return list;
    }

    @Tool("有教师姓名时，必须第一个执行")
    public String getTeacherInfo(@P("教师姓名") String teacherName) {
        log.info("teacherName:{}", teacherName);
        Map<String, String> teacherInfoByName = (HashMap<String, String>) (teacherClient.getTeacherInfoByName(teacherName).getData());
        return String.format("教师姓名：%s,教师id：%s,教师邮箱：%s,教师电话：%s,教师生日：%s,教师性别：%s,教师学院：%s",
                teacherInfoByName.get("name"), teacherInfoByName.get("teacherid"), teacherInfoByName.get("email")
                , teacherInfoByName.get("phone"), teacherInfoByName.get("birthday"), teacherInfoByName.get("sex")
                , teacherInfoByName.get("college"));
    }

    @Tool("某个教师视频列表，请根据返回的视频id组装URL：http://localhost:3000/video/视频id，请返回url显示时显示名称为视频名称")
    public List<String> getTeacherVideos(@P("教师的teacherid") String teacherid) {
        log.info("teacherid:{}", teacherid);
        ArrayList<?> data = (ArrayList<?>) videoClient.selectVideoByTeacher(teacherid).getData();
        return getVideoString(data);
    }

    @Tool("什么最高或最多的前多少个或者什么最低或最少的前多少个，播放取值views，点赞取like_num，收藏取collect_num，取高时order取desc，否则取asc，请返回url显示时显示名称为视频名称")
    public List<String> getVideoTopField(@P("请根据需要自行决定查询页数，默认为1") int pageNumber, @P("前多少个") int top, @P("取播放点赞收藏其中一个值") String field, @P("取高或低") String order) {
        log.info("pageNumber:{},top:{},field:{},order:{}", pageNumber, top, field, order);
        ArrayList<?> data = (ArrayList<?>) videoClient.getVideoTopByField(pageNumber, top, field, order).getData();
        return getVideoString(data);
    }

    @Tool("有关什么的视频或者与什么相关的多少个视频等类似问题，请返回url显示时显示名称为视频名称")
    public List<String> getVideoByName(@P("请根据需要自行决定查询页数，默认为1") int pageNumber, @P("前多少个，如未识别到请取5") int top, @P("搜索条件") String name) {
        log.info("pageNumber:{},top:{},type:{}", pageNumber, top, name);
        ArrayList<?> data = (ArrayList<?>) videoClient.getVideoByName(pageNumber, top, name).getData();
        return getVideoString(data);
    }

    @Tool("什么类型的视频或者什么类型的前几个视频，请返回url显示时显示名称为视频名称")
    public List<String> getVideoByType(@P("请根据需要自行决定查询页数，默认为1") int pageNumber, @P("前多少个，如未识别到请取5") int top, @P("搜索条件") String type) {
        log.info("pageNumber:{},top:{},type:{}", pageNumber, top, type);
        ArrayList<?> data = (ArrayList<?>) videoClient.getVideoByType(pageNumber, top, type).getData();
        return getVideoString(data);
    }


}