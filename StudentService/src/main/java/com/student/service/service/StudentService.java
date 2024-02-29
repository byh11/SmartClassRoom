package com.student.service.service;


import com.student.entity.Student;
import org.com.execption.MyException;

public interface StudentService {
    void Register(Student student) throws MyException;

    Student Login(String studentid, String password) throws MyException;
}
