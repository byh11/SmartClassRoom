package org.com.controller;

import org.com.entity.Result;
import org.com.entity.Teacher;
import org.com.execption.MyException;

import org.com.service.TeacherServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/teacher")
public class TeacherController {
    @Autowired
    private TeacherServiceImpl teacherService;

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public Result<String> Register(Teacher teacher) throws MyException {
        System.out.println(teacher.toString());
        teacherService.Register(teacher);
        return Result.success("注册成功");
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public Result<Teacher> Login(String teacherid, String password) throws MyException {
        Teacher teacher=teacherService.Login(teacherid,password);
        return Result.success("登录成功",teacher);
    }

    @RequestMapping(value = "/AttendClazz",method = RequestMethod.POST)
    @ResponseBody
    public Result<String> AttendClazz() throws MyException{
        teacherService.AttendClazz();
        return Result.success("上课成功");
    }

    @RequestMapping(value = "FinishClazz", method = RequestMethod.POST)
    @ResponseBody
    public Result<String> FinishClazz() throws MyException{
        teacherService.FinishClazz();
        return Result.success("下课成功");
    }
}
