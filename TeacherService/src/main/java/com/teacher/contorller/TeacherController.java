package com.teacher.contorller;



import com.teacher.client.VideoClient;
import com.teacher.entity.Teacher;
import com.teacher.entity.Video;
import lombok.extern.slf4j.Slf4j;
import org.com.entity.Result;
import org.com.execption.MyException;
import com.teacher.service.TeacherServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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
    public Result<String> Register(Teacher teacher) {
        System.out.println(teacher.toString());
        try {
            teacherService.Register(teacher);
        } catch (MyException e) {
            log.info(e.getMessage());
            return Result.error(e.getMessage());
        }
        return Result.success("注册成功");
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public Result<Teacher> Login(String teacherid, String password) {
        Teacher teacher= null;
        try {
            teacher = teacherService.Login(teacherid,password);
        } catch (MyException e) {
            log.info(e.getMessage());
            return Result.error(e.getMessage());
        }
        return Result.success("登录成功",teacher);
    }

    @RequestMapping(value = "/AttendClazz",method = RequestMethod.POST)
    @ResponseBody
    public Result<String> AttendClazz(String teacherid,String clazzname) throws MyException{
        teacherService.AttendClazz(teacherid,clazzname);
        return Result.success("上课成功");
    }

    @RequestMapping(value = "FinishClazz", method = RequestMethod.POST)
    @ResponseBody
    public Result<String> FinishClazz(String teacherid) throws MyException{
        teacherService.FinishClazz(teacherid);
        return Result.success("下课成功");
    }
}
