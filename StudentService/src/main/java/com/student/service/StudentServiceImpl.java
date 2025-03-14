package com.student.service;

import com.google.gson.Gson;
import com.student.entity.Student;
import com.student.mapper.Redis;
import com.student.mapper.StudentMapper;
import com.student.service.service.StudentService;
import org.com.execption.MyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private Redis redis;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private Gson gson;

    @Override
    public void Register(Student student) throws MyException {
        if (redis.isExist(student.getStudentid())) {
            throw new MyException("用户名已存在");
        }
        if (studentMapper.SelectById(student.getStudentid()) != null) {
            redis.registerSet(student.getStudentid(), gson.toJson(student));
            throw new MyException("用户名已存在");
        }
        studentMapper.Insert(student.getStudentid(), student.getPassword(), student.getName(), student.getPhone(),
                student.getEmail(), student.getBirthday(), student.getSex(), student.getClazz(), student.getMajor());
        redis.registerSet(student.getStudentid(), gson.toJson(student));
    }

    @Override
    public Student Login(String studentid, String password) throws MyException {
        Student student;
        if (redis.isExist(studentid)) {
            student = gson.fromJson(redis.getKey(studentid), Student.class);
        } else {
            student = studentMapper.SelectByStudent(studentid);
        }
        if (student == null) {
            throw new MyException("当前用户不存在");
        }
        if (student.getPassword().equals(password)) {
            return student;
        } else {
            throw new MyException("密码错误");
        }
    }
}
