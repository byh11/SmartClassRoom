package com.teacher.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("teacher")
public class Teacher {
    @TableId(value = "teacherid")
    private String teacherid;
    private String password;
    private String name;
    private String phone;
    private String email;
    private Date birthday;
    private String sex;
    private String college;
}
