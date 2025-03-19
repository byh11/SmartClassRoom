package com.student.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.student.entity.Student;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StudentMapper extends BaseMapper<Student> {
//    @Insert("insert into student(studentid,password,name,phone,email,birthday,sex,clazz,major) " +
//            "values(#{studentid},#{password},#{name},#{phone},#{email},#{birthday},#{sex},#{clazz},#{major})")
//    void Insert(String studentid, String password, String name, String phone, String email,
//                Date birthday, String sex, String clazz, String major);
//
//    @Select("select studentid from student where studentid=#{studentid}")
//    Boolean SelectById(String studentid);
//
//    @Select("select * from student where studentid=#{studentid}")
//    Student SelectByStudent(String studentid);
}
