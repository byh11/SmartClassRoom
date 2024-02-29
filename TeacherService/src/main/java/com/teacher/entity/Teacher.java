package com.teacher.entity;

import lombok.*;

import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Teacher {
    private String teacherid;
    private String password;
    private String name;
    private String phone;
    private String email;
    private Date birthday;
    private String sex;
    private String college;
}
