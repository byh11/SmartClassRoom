package com.fox.client;

import com.fox.entity.Result;
import com.fox.entity.Teacher;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@FeignClient(name = "teacher-service", url = "http://10.0.220.99:12000")
public interface TeacherClient {

    // 测试接口
    @GetMapping("/teacher/test")
    Result<String> test();

    // 注册接口
    @PostMapping("/teacher/register")
    Result<String> register(@RequestBody Teacher teacher);

    // 登录接口
    @PostMapping("/teacher/login")
    Result<Teacher> login(
            @RequestParam("teacherid") String teacherid,
            @RequestParam("password") String password
    );

    // 修改密码
    @PostMapping("/teacher/{teacherid}/change-password")
    Result<?> changePassword(
            @PathVariable("teacherid") String teacherid,
            @RequestBody Map<String, String> requestBody
    );

    // 获取教师信息
    @GetMapping("/teacher/{teacherid}/info")
    Result<?> getTeacherInfo(@PathVariable("teacherid") String teacherid);

    // 更新教师信息
    @PostMapping("/teacher/{teacherid}/info")
    Result<?> postTeacherInfo(
            @PathVariable("teacherid") String teacherid,
            @RequestBody Teacher requestBody
    );

    // 开始上课
    @PostMapping("/teacher/{teacherid}/class/start")
    Result<String> attendClazz(
            @PathVariable("teacherid") String teacherid,
            @RequestParam("className") String className
    );

    // 结束上课并上传视频
    @PostMapping("/teacher/{teacherid}/class/end")
    Result<String> finishClazz(
            @PathVariable("teacherid") String teacherid,
            @RequestPart("video") MultipartFile video,
            @RequestParam("className") String className,
            @RequestParam("courseName") String courseName,
            @RequestParam("startTime") String startTime,
            @RequestParam("endTime") String endTime
    );

    // 获取视频记录
    @GetMapping("/teacher/{teacherid}/video")
    Result<?> getVideos(@PathVariable("teacherid") String teacherid);

    // 上传头像
    @PostMapping("/teacher/{teacherid}/avatar")
    Result<?> avatarUpload(
            @PathVariable("teacherid") String teacherid,
            @RequestPart("file") MultipartFile file
    );
}