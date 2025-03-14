package com.teacher.mapper;

import com.teacher.entity.Teacher;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Date;

@Mapper
public interface TeacherMapper {
    @Insert("insert into teacher(teacherid,password,name,phone,email,birthday,sex,college) " +
            "values(#{teacherid},#{password},#{name},#{phone},#{email},#{birthday},#{sex},#{college})")
    void Insert(String teacherid, String password, String name, String phone, String email, Date birthday, String sex, String college);

    @Select("select teacherid from teacher where teacherid=#{teacherid}")
    Boolean SelectById(String teacherid);

    @Select("select * from teacher where teacherid=#{teacherid}")
    Teacher SelectByTeacher(String teacherid);
}
