package com.teacher.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("classrecord")
public class ClassRecord {
    @TableId(value = "id")
    private long id;
    private String teacherId;
    private String className;
    private String videoId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalTime duration;
    private String clazz;
    private int status;
}
