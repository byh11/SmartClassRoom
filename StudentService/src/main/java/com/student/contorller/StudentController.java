package com.student.contorller;

import com.student.entity.Student;
import com.student.service.StudentServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.com.entity.Result;
import org.com.execption.MyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Controller
@RequestMapping("/student")
@Slf4j
public class StudentController {

    @Autowired
    private StudentServiceImpl studentService;

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> test() {
        System.out.println("111111111111111111");
        return Result.success("测试成功");
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> register(@RequestBody Student student) {
        System.out.println(student.toString());
        try {
            studentService.register(student);
        } catch (MyException e) {
            log.info(e.getMessage());
            return Result.error(e.getMessage());
        }
        return Result.success("注册成功");
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> login(@RequestParam("studentid") String studentid, @RequestParam("password") String password) {
        try {
            Student student = studentService.login(studentid, password);
            return Result.success("登录成功", student);
        } catch (MyException e) {
            log.info(e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/{studentid}/change-password")
    @ResponseBody
    public Result<?> changePassword(@PathVariable String studentid, @RequestBody Map<String, String> requestBody) {
        try {
            String result = studentService.updatePassword(studentid, requestBody.get("oldPassword"), requestBody.get("newPassword"));
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{studentid}/info")
    @ResponseBody
    public Result<?> getStudentInfo(@PathVariable String studentid) {
        try {
            Student result = studentService.getStudentInfo(studentid);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/{studentid}/info")
    @ResponseBody
    public Result<?> postStudentInfo(@PathVariable String studentid, @RequestBody Student requestBody) {
        try {
            Student result = studentService.updateStudentInfo(studentid, requestBody);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @RequestMapping(value = "/{studentid}/avatar", method = RequestMethod.POST)
    @ResponseBody
    public Result<?> avatarUpload(@PathVariable String studentid, @RequestPart("file") MultipartFile file) throws MyException {
        try {
            String url = studentService.avatarUpload(studentid, file);
            return Result.success(url);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }


}
