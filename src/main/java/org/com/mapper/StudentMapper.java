package org.com.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.com.entity.Student;

import java.util.Date;

@Mapper
public interface StudentMapper {
    @Insert("insert into student(studentid,password,name,phone,email,birthday,sex,clazz,major) " +
            "values(#{studentid},#{password},#{name},#{phone},#{email},#{birthday},#{sex},#{clazz},#{major})")
    void Insert(String studentid, String password, String name, String phone, String email,
                Date birthday, String sex, String clazz, String major);

    @Select("select studentid from student where studentid=#{studentid}")
    Boolean SelectById(String studentid);

    @Select("select * from student where studentid=#{studentid}")
    Student SelectByStudent(String studentid);
}
