package org.com.controller;

import org.com.entity.Result;
import org.com.entity.Student;
import org.com.execption.MyException;
import org.com.service.StudentServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private StudentServiceImpl studentService;

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public Result<String> Register(Student student) throws MyException {
        System.out.println(student.toString());
        studentService.Register(student);
        return Result.success("注册成功");
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public Result<Student> Login(String studentid, String password) throws MyException {
        Student student=studentService.Login(studentid,password);
        return Result.success("登录成功",student);
    }
}
