package com.teacher.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
