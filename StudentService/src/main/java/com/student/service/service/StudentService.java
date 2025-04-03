package com.student.service.service;


import com.student.entity.Student;
import org.com.execption.MyException;
import org.springframework.web.multipart.MultipartFile;

public interface StudentService {
    void register(Student student) throws MyException;

    Student login(String studentid, String password) throws MyException;

    String updatePassword(String studentid, String oldPassword, String newPassword) throws MyException;

    Student getStudentInfo(String studentid) throws MyException;

    Student updateStudentInfo(String studentid, Student student) throws MyException;

    String avatarUpload(String studentid, MultipartFile file) throws MyException;
}
