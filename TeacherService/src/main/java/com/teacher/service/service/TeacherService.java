package com.teacher.service.service;


import com.teacher.entity.Teacher;
import org.com.execption.MyException;

public interface TeacherService {
    void register(Teacher teacher) throws MyException;

    Teacher login(String teacherid, String password) throws MyException;

    void attendClazz(String teacherid, String clazzname);

    void finishClazz(String teacherid);
}
