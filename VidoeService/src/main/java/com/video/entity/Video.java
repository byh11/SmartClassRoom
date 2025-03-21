package com.video.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("video")
public class Video {
    @TableId(value = "id")
    private long id;
    private String teacherid;
    private String url;
    private String videoname;
    private String like;
    private String collect;
    private String commentid;
}
