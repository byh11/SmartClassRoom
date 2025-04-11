package com.video.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @author byh
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentList extends Comment {
    private String avatar;
    private String userName;
}
