package com.student.service.service;


import com.student.entity.Student;
import org.com.execption.MyException;

public interface StudentService {
    void register(Student student) throws MyException;

    Student login(String studentid, String password) throws MyException;
}
