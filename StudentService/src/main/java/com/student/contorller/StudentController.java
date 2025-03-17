package com.student.contorller;

import com.student.entity.Student;
import com.student.service.StudentServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.com.entity.Result;
import org.com.execption.MyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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
    public Result<String> register(@RequestBody Student student) {
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
    public Result<Student> login(String studentid, String password) {
        Student student = null;
        try {
            student = studentService.login(studentid, password);
        } catch (MyException e) {
            log.info(e.getMessage());
            return Result.error(e.getMessage());
        }
        return Result.success("登录成功", student);
    }
}
