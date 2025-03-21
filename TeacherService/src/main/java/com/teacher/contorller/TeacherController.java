package com.teacher.contorller;


import com.teacher.client.VideoClient;
import com.teacher.entity.ClassRecord;
import com.teacher.entity.Teacher;
import com.teacher.entity.Video;
import com.teacher.service.TeacherServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.com.entity.Result;
import org.com.execption.MyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/teacher")
@Slf4j
public class TeacherController {
    @Autowired
    private TeacherServiceImpl teacherService;

    @Autowired
    private VideoClient videoClient;

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> test() {
        System.out.println("22222222222222222222");
        Video video = new Video();
        video.setTeacherid("1");
        video.setUrl("1");
        video.setVideoname("1");
        video.setId(1L);
        videoClient.SaveVideo(video);
        return Result.success("测试成功");
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public Result<String> register(@RequestBody Teacher teacher) {
        System.out.println(teacher.toString());
        try {
            teacherService.register(teacher);
        } catch (MyException e) {
            log.info(e.getMessage());
            return Result.error(e.getMessage());
        }
        return Result.success("注册成功");
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public Result<Teacher> login(@RequestParam("teacherid") String teacherid, @RequestParam("password") String password) {
        Teacher teacher = null;
        try {
            teacher = teacherService.login(teacherid, password);
        } catch (MyException e) {
            log.info(e.getMessage());
            return Result.error(e.getMessage());
        }
        return Result.success("登录成功", teacher);
    }

    @PostMapping("/{teacherid}/change-password")
    @ResponseBody
    public Result<?> changePassword(@PathVariable String teacherid, @RequestBody Map<String, String> requestBody) {
        try {
            String result = teacherService.updatePassword(teacherid, requestBody.get("oldPassword"), requestBody.get("newPassword"));
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{teacherid}/info")
    @ResponseBody
    public Result<?> getStudentInfo(@PathVariable String teacherid) {
        try {
            Teacher result = teacherService.getTeacherInfo(teacherid);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/{teacherid}/info")
    @ResponseBody
    public Result<?> postStudentInfo(@PathVariable String teacherid, @RequestBody Teacher requestBody) {
        try {
            Teacher result = teacherService.updateTeacherInfo(teacherid, requestBody);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @RequestMapping(value = "/{teacherid}/class/start", method = RequestMethod.POST)
    @ResponseBody
    public Result<String> attendClazz(@PathVariable String teacherid, @RequestParam("className") String clazzname) throws MyException {
        teacherService.attendClazz(teacherid, clazzname);
        return Result.success("上课成功");
    }

    @RequestMapping(value = "/{teacherid}/class/end", method = RequestMethod.POST)
    @ResponseBody
    public Result<String> finishClazz(@PathVariable String teacherid,
                                      @RequestPart("video") MultipartFile video,
                                      @RequestParam("className") String className,
                                      @RequestParam("courseName") String courseName,
                                      @RequestParam("startTime") String startTime,
                                      @RequestParam("endTime") String endTime
    ) throws MyException {
        teacherService.finishClazz(teacherid, video, className, courseName, startTime, endTime);
        return Result.success("下课成功");
    }

    @RequestMapping(value = "/{teacherid}/class/records", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> records(@PathVariable String teacherid, @RequestParam("pageNumber") int pageNumber) throws MyException {
        try {
            List<ClassRecord> result = teacherService.getClassRecord(teacherid, pageNumber);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
