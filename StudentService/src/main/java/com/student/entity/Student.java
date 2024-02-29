package com.student.entity;

import lombok.*;

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
