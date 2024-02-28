package org.com.mapper;

import org.apache.ibatis.annotations.*;
import org.com.entity.Video;

import javax.sql.RowSet;
import java.util.ArrayList;

@Mapper
public interface VideoMapper {

    @Insert("insert into video(id,teacherid,url,videoname) values(#{id},#{teacherid},#{url},#{videoname})")
    void insert(long id, String teacherid, String url, String videoname);


    @Select("select * from video where videoname like '%${videoname}%'")
    ArrayList<Video> SelectVideo(@Param("videoname")String videoname);

    @Delete("delete from video where id=#{id}")
    void DeleteVideo(String id);

    @Select("select * from video where id=#{id}")
    Video SelectById(String id);
}
