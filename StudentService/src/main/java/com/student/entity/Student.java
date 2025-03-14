package com.student.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Student {
    private String studentid;
    private String password;
    private String name;
    private String phone;
    private String email;
    private Date birthday;
    private String sex;
    private String clazz;
    private String major;
}
