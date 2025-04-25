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
@TableName("learning_progress")
public class LearningProgress {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String studentId;
    private Long videoId;
    private Integer watchDuration;
    private Float progress;
    private Integer lastPosition;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}