package com.video.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.video.entity.Video;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface VideoMapper extends BaseMapper<Video> {

//    @Insert("insert into video(id,teacherid,url,videoname) values(#{id},#{teacherid},#{url},#{videoname})")
//    void insert(long id, String teacherid, String url, String videoname);
//
//
//    @Select("select * from video where videoname like '%${videoname}%'")
//    ArrayList<Video> SelectVideo(@Param("videoname")String videoname);
//
//    @Select("select * from video")
//    ArrayList<Video> SelectVideoAll(RowBounds rowBounds);
//
//    @Delete("delete from video where id=#{id}")
//    void DeleteVideo(String id);
//
//    @Select("select * from video where id=#{id}")
//    Video SelectById(String id);
}
