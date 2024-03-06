package com.teacher.service;


import com.teacher.config.Yun;
import com.teacher.entity.Teacher;
import com.google.gson.Gson;
import com.teacher.service.service.TeacherService;
import com.teacher.mapper.TeacherMapper;
import org.com.execption.MyException;
import com.teacher.mapper.Redis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class TeacherServiceImpl implements TeacherService {

    @Autowired
    private Redis redis;

    @Autowired
    private TeacherMapper teacherMapper;

    @Autowired
    private Gson gson;

    @Autowired
    private Yun yun;

    ConcurrentHashMap<String,Boolean> map=new ConcurrentHashMap<>();

    @Override
    public void Register(Teacher teacher) throws MyException {
        if(redis.isExist(teacher.getTeacherid())){
            throw new MyException("用户名已存在");
        }
        if(teacherMapper.SelectById(teacher.getTeacherid())!=null){
            redis.registerSet(teacher.getTeacherid(),gson.toJson(teacher));
            throw new MyException("用户名已存在");
        }
        teacherMapper.Insert(teacher.getTeacherid(),teacher.getPassword(),teacher.getName(),teacher.getPhone(),
                teacher.getEmail(),teacher.getBirthday(),teacher.getSex(),teacher.getCollege());
        redis.registerSet(teacher.getTeacherid(),gson.toJson(teacher));
    }

    @Override
    public Teacher Login(String teacherid, String password) throws MyException {
        Teacher teacher;
        if(redis.isExist(teacherid)){
            teacher=gson.fromJson(redis.getKey(teacherid),Teacher.class);
        }else {
            teacher=teacherMapper.SelectByTeacher(teacherid);
        }
        if(teacher==null){
            throw new MyException("当前用户不存在");
        }
        if(teacher.getPassword().equals(password)){
            return teacher;
        }else {
            throw new MyException("密码错误");
        }
    }

    @Override
    public void AttendClazz(String teacherid) {
        String id= yun.getId();
        String key= yun.getKey();
        map.put(teacherid,true);
    }

    @Override
    public void FinishClazz(String teacherid) {

    }




}
