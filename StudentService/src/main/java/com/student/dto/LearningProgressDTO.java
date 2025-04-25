package com.student.dto;

import lombok.Data;

/**
 * @author byh
 */
@Data
public class LearningProgressDTO {

    private String studentId;
    private Long videoId;
    private Integer watchDuration;
    private Float progress;
    private Integer lastPosition;

}
