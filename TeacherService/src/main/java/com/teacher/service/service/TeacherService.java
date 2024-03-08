package com.teacher.service.service;


import com.teacher.entity.Teacher;
import org.com.execption.MyException;

public interface TeacherService {
    void Register(Teacher teacher) throws MyException;

    Teacher Login(String teacherid, String password) throws MyException;

    void AttendClazz(String teacherid,String clazzname);

    void FinishClazz(String teacherid);
}
