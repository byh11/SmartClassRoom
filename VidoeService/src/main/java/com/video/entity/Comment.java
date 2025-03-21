package com.video.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("comment")
public class Comment {
    @TableId("commentid")
    private long commentid;
    @TableId("videoid")
    private long videoid;
    @TableId("userid")
    private String userid;
    @TableId("content")
    private String content;
    @TableId("parentid")
    private String parentid;
}
