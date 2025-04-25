package com.student.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author byh
 */
@Data
@TableName("learning_stats")
public class LearningStats {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String studentId;
    private Integer totalWatchTime;
    private Integer completedVideos;
    private Integer totalVideos;
    private LocalDateTime lastStudyDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}