package com.video.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.video.entity.Comment;
import org.mapstruct.Mapper;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
}
