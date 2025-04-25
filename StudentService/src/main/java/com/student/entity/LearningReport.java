package com.student.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author byh
 */
@Data
@TableName("learning_report")
public class LearningReport {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String studentId;
    private Integer reportType;
    private LocalDate reportDate;
    private Integer watchTime;
    private Integer completedVideos;
    private String suggestions;
    private LocalDateTime createdAt;
}