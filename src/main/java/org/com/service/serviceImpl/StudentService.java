package org.com.service.serviceImpl;

import org.com.entity.Student;
import org.com.execption.MyException;

public interface StudentService {
    void Register(Student student) throws MyException;

    Student Login(String studentid, String password) throws MyException;
}
