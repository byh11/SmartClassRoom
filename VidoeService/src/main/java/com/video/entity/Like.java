package com.video.entity;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author byh
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("likes")
public class Like {
    @TableId("id")
    private long id;
    private String userId;
    private long videoId;
    private int userType;
}
