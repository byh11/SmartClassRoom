package com.teacher.service.service;


import com.teacher.entity.Teacher;
import org.com.execption.MyException;

public interface TeacherService {
    void register(Teacher teacher) throws MyException;

    Teacher login(String teacherid, String password) throws MyException;

    String updatePassword(String teacherid, String oldPassword, String newPassword) throws MyException;

    Teacher getTeacherInfo(String teacherid) throws MyException;

    Teacher updateTeacherInfo(String teacherid, Teacher teacher) throws MyException;

    void attendClazz(String teacherid, String clazzname);

    void finishClazz(String teacherid);
}
