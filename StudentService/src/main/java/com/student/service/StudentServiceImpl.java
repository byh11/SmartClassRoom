package com.student.service;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.google.gson.Gson;
import com.student.Util.COSUtil;
import com.student.entity.Student;
import com.student.mapper.StudentMapper;
import com.student.service.service.StudentService;
import org.com.execption.MyException;
import org.com.mapper.Redis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private Redis redis;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private Gson gson;

    @Autowired
    private COSUtil cosUtil;

    @Override
    public void register(Student student) throws MyException {
        if (student.getStudentid().isEmpty() || student.getPassword().isEmpty()) {
            throw new MyException("用户名或密码不能为空");
        }
        if (redis.isExist(student.getStudentid())) {
            throw new MyException("该用户已注册，请勿重复注册");
        }
//        if (studentMapper.SelectById(student.getStudentid()) != null) {
//            redis.setKey(student.getStudentid(), gson.toJson(student));
//            throw new MyException("用户名已存在");
//        }
        try {
            studentMapper.insert(student);
//            studentMapper.Insert(student.getStudentid(), student.getPassword(), student.getName(), student.getPhone(),
//                    student.getEmail(), student.getBirthday(), student.getSex(), student.getClazz(), student.getMajor());
            try {
                redis.setKey(student.getStudentid(), gson.toJson(student));
            } catch (Exception e) {

            }
        } catch (DuplicateKeyException e) {
            if (e.getCause() instanceof SQLException) {
                SQLException sqlEx = (SQLException) e.getCause();
                if (sqlEx.getErrorCode() == 1062) {
                    throw new MyException("该用户已注册，请勿重复注册");
                }
            }
        } catch (Exception e) {
            throw new MyException("系统异常，请稍后重试");
        }

    }

    @Override
    public Student login(String studentid, String password) throws MyException {
        if (studentid.isEmpty() || password.isEmpty()) {
            throw new MyException("用户名或密码不能为空");
        }
        Student student;
        if (redis.isExist(studentid)) {
            student = gson.fromJson(redis.getKey(studentid), Student.class);
        } else {
//            student = studentMapper.SelectByStudent(studentid);
            LambdaUpdateWrapper<Student> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(Student::getStudentid, studentid);
            student = studentMapper.selectOne(updateWrapper);
        }
        if (student == null) {
            throw new MyException("当前用户不存在");
        }
        if (student.getPassword().equals(password)) {
            return student;
        } else {
            throw new MyException("密码错误");
        }
    }

    @Override
    public String updatePassword(String studentid, String oldPassword, String newPassword) throws MyException {
        try {
            login(studentid, oldPassword);
        } catch (MyException e) {
            throw new MyException("原密码错误，修改失败");
        }
        LambdaUpdateWrapper<Student> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Student::getStudentid, studentid) // 主键条件
                .set(Student::getPassword, newPassword); // 设置新密码

        int rows = studentMapper.update(null, updateWrapper); // 第一个参数传 null，仅使用 wrapper
        if (rows > 0) {
            redis.deleteKey(studentid);
            return "修改成功";
        }
        return "修改失败";
    }

    @Override
    public Student getStudentInfo(String studentid) throws MyException {
        if (redis.isExist(studentid)) {
            return gson.fromJson(redis.getKey(studentid), Student.class);
        }
        LambdaUpdateWrapper<Student> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Student::getStudentid, studentid);
        Student student = studentMapper.selectOne(updateWrapper);
        if (student != null) {
            return student;
        }
        return null;
    }

    @Override
    public Student updateStudentInfo(String studentid, Student student) throws MyException {
        LambdaUpdateWrapper<Student> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Student::getStudentid, studentid);
        if (studentMapper.update(student, updateWrapper) > 0) {
            redis.deleteKey(studentid);
            redis.setKey(studentid, gson.toJson(student));
            return student;
        }
        return student;
    }

    public static void deleteFile(String filePath) throws IOException {
        // 创建代表目标文件的File对象
        File fileToDelete = new File(filePath);

        // 检查文件是否存在并且是一个文件（而不是目录）
        if (fileToDelete.exists() && fileToDelete.isFile()) {
            // 尝试删除文件
            boolean isDeleted = fileToDelete.delete();

            if (isDeleted) {
                System.out.println("文件删除成功: " + filePath);
            } else {
                throw new IOException("未能删除文件: " + filePath);
            }
        } else {
            throw new IOException("文件不存在或路径指向的是一个目录: " + filePath);
        }
    }

    @Override
    public String avatarUpload(String studentid, MultipartFile file) throws MyException {
        String path = "C:\\videoFile\\" + studentid + "_avatar.jpg";
        try {
            saveFile(file, path);
            cosUtil.putObject("avatar/" + studentid + "_avatar.jpg", path);
            String url = cosUtil.getURL("avatar/" + studentid + "_avatar.jpg");
            Student student = studentMapper.selectById(studentid);
            student.setAvatar(url);
            studentMapper.updateById(student);
            redis.setKey(studentid, gson.toJson(student));
            return url;
        } catch (IOException e) {
            throw new MyException("文件保存失败");
        } finally {
            try {
                deleteFile(path);
            } catch (IOException e) {
                throw new MyException("文件删除失败");
            }
        }
    }

    private void saveFile(MultipartFile file, String path) throws IOException {
        File destFile = new File(path);
        File parentDir = destFile.getParentFile();

        if (!parentDir.exists()) {
            parentDir.mkdirs(); // 创建必要的父目录
        }

        // 使用NIO保存文件
        Files.copy(file.getInputStream(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

}
