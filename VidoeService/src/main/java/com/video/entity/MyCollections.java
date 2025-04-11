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
@TableName("collections")
public class MyCollections {
    @TableId("id")
    private long id;
    private String userId;
    private long videoId;
    private int userType;
}
