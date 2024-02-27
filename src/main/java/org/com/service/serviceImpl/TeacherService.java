package org.com.service.serviceImpl;

import org.com.entity.Student;
import org.com.entity.Teacher;
import org.com.execption.MyException;

public interface TeacherService {
    void Register(Teacher teacher) throws MyException;

    Teacher Login(String teacherid, String password) throws MyException;

    void AttendClazz();

    void FinishClazz();
}
