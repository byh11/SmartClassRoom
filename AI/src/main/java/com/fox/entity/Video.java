package com.fox.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Video {
    private Long videoid;
    private String teacherid;
    private String url;
    private String videoName;
    private Long likeNum;
    private Long collectNum;
    private LocalTime duration;
    private int status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String className;
    private String videoType;
    private String videoText;
    private Long views;
    private String coverUrl;
}
