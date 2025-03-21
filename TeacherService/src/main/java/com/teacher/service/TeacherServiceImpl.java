package com.teacher.service;


import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.qcloud.vod.VodUploadClient;
import com.qcloud.vod.model.VodUploadRequest;
import com.qcloud.vod.model.VodUploadResponse;
import com.teacher.client.VideoClient;
import com.teacher.config.Yun;
import com.teacher.entity.ClassRecord;
import com.teacher.entity.Teacher;
import com.teacher.mapper.ClassRecordMapper;
import com.teacher.mapper.TeacherMapper;
import com.teacher.service.service.TeacherService;
import com.tencentcloudapi.asr.v20190614.AsrClient;
import com.tencentcloudapi.asr.v20190614.models.CreateRecTaskRequest;
import com.tencentcloudapi.asr.v20190614.models.CreateRecTaskResponse;
import com.tencentcloudapi.asr.v20190614.models.DescribeTaskStatusRequest;
import com.tencentcloudapi.asr.v20190614.models.DescribeTaskStatusResponse;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.vod.v20180717.VodClient;
import com.tencentcloudapi.vod.v20180717.models.DeleteMediaRequest;
import com.tencentcloudapi.vod.v20180717.models.DeleteMediaResponse;
import lombok.extern.slf4j.Slf4j;
import org.com.execption.MyException;
import org.com.mapper.Redis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class TeacherServiceImpl implements TeacherService {

    @Autowired
    private Redis redis;

    @Autowired
    private TeacherMapper teacherMapper;

    @Autowired
    private Gson gson;

    @Autowired
    private Yun yun;

    @Autowired
    private VideoClient videoClient;

    @Autowired
    private ClassRecordMapper classRecordMapper;

    private ConcurrentHashMap<String, Boolean> map = new ConcurrentHashMap<>();

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
    public void register(Teacher teacher) throws MyException {
        if (teacher.getTeacherid().isEmpty() || teacher.getPassword().isEmpty()) {
            log.info("用户名或密码为空");
            throw new MyException("用户名或密码为空");
        }
        if (redis.isExist(teacher.getTeacherid())) {
            log.info("用户名已存在，请勿重复注册");
            throw new MyException("用户已存在，请勿重复注册");
        }
//        if (teacherMapper.SelectById(teacher.getTeacherid()) != null) {
//            redis.setKey(teacher.getTeacherid(), gson.toJson(teacher));
//            log.info("用户名已存在，请勿重复注册");
//            throw new MyException("用户名已存在，请勿重复注册");
//        }
        try {
            teacherMapper.insert(teacher);
//            teacherMapper.Insert(teacher.getTeacherid(), teacher.getPassword(), teacher.getName(), teacher.getPhone(),
//                    teacher.getEmail(), teacher.getBirthday(), teacher.getSex(), teacher.getCollege());
            try {
                redis.setKey(teacher.getTeacherid(), gson.toJson(teacher));
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
    public Teacher login(String teacherid, String password) throws MyException {
        if (teacherid.isEmpty() || password.isEmpty()) {
            log.info("用户名或密码为空");
            throw new MyException("用户名或密码为空");
        }
        Teacher teacher;
        if (redis.isExist(teacherid)) {
            teacher = gson.fromJson(redis.getKey(teacherid), Teacher.class);
        } else {
            LambdaUpdateWrapper<Teacher> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(Teacher::getTeacherid, teacherid);
            teacher = teacherMapper.selectOne(updateWrapper);
//            teacher = teacherMapper.SelectByTeacher(teacherid);
        }
        if (teacher == null) {
            log.info("当前用户不存在");
            throw new MyException("当前用户不存在");
        }
        if (teacher.getPassword().equals(password)) {
            return teacher;
        } else {
            log.info("密码错误");
            throw new MyException("密码错误");
        }
    }

    @Override
    public String updatePassword(String teacherid, String oldPassword, String newPassword) throws MyException {
        try {
            login(teacherid, oldPassword);
        } catch (MyException e) {
            throw new MyException("原密码错误，修改失败");
        }
        LambdaUpdateWrapper<Teacher> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Teacher::getTeacherid, teacherid) // 主键条件
                .set(Teacher::getPassword, newPassword); // 设置新密码

        int rows = teacherMapper.update(null, updateWrapper); // 第一个参数传 null，仅使用 wrapper
        if (rows > 0) {
            redis.deleteKey(teacherid);
            return "修改成功";
        }
        return "修改失败";
    }

    @Override
    public Teacher getTeacherInfo(String teacherid) throws MyException {
        if (redis.isExist(teacherid)) {
            return gson.fromJson(redis.getKey(teacherid), Teacher.class);
        }
        LambdaUpdateWrapper<Teacher> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Teacher::getTeacherid, teacherid);
        Teacher teacher = teacherMapper.selectOne(updateWrapper);
        if (teacher != null) {
            return teacher;
        }
        return null;
    }

    @Override
    public Teacher updateTeacherInfo(String teacherid, Teacher teacher) throws MyException {
        LambdaUpdateWrapper<Teacher> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Teacher::getTeacherid, teacherid);
        if (teacherMapper.update(teacher, updateWrapper) > 0) {
            redis.deleteKey(teacherid);
            redis.setKey(teacherid, gson.toJson(teacher));
            return teacher;
        }
        return teacher;
    }

    @Override
    public void attendClazz(String teacherid, String clazzname) {
        String id = yun.getId();
        String key = yun.getKey();
        map.put(teacherid, true);
//        String path = "/opt/SmartClassRoom/11/" + teacherid + clazzname + ".mp4";
//        new Thread(() -> {
//            try {
//                record(teacherid, path);
//                ArrayList<String> video = upload(path);
//                long taskId = request(video.get(1));
//                response(clazzname, taskId);
//                synthesis(teacherid, clazzname, path);
//                deleteVideo(video.get(0));
//                video = upload("/opt/SmartClassRoom/11/" + clazzname + ".mp4");
//                File file = new File(path);
//                file.delete();
//                File file1 = new File("/opt/SmartClassRoom/11/" + clazzname + ".srt");
//                file1.delete();
//                File file2 = new File("/opt/SmartClassRoom/11/" + clazzname + ".mp4");
//                file2.delete();
//                //远程调用保存Video信息
//                Video video1 = new Video();
//                video1.setId(Long.parseLong(video.get(0)));
//                video1.setUrl(teacherid);
//                video1.setUrl(video.get(1));
//                video1.setVideoname(clazzname);
//                videoClient.SaveVideo(video1);
//            } catch (MyException e) {
//                log.error(e.getMessage());
//                throw new RuntimeException(e);
//            }
//        }).start();

    }

    @Override
    public void finishClazz(String teacherid, MultipartFile video, String className, String courseName, String startTime, String endTime) {
        ClassRecord classRecord = new ClassRecord();
        classRecord.setTeacherId(teacherid);
        classRecord.setClassName(courseName);
        classRecord.setClazz(className);
        classRecord.setStartTime(LocalDateTime.parse(startTime));
        classRecord.setEndTime(LocalDateTime.parse(endTime));
        Duration duration = Duration.between(LocalTime.parse(startTime), LocalTime.parse(endTime));
        // 如果开始时间在结束时间之后（例如跨午夜的情况），调整duration
        if (duration.isNegative()) {
            duration = duration.plusDays(1);
        }
        // 转换为总秒数，然后获取小时和分钟
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        classRecord.setDuration(LocalTime.parse(String.format("%02d:%02d", hours, minutes)));

        try {
            String path = "videoFile\\" + teacherid + ".mp4";
            saveFile(video, path);
            upload("videoFile" + teacherid + ".mp4");
            deleteFile(path);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        map.put(teacherid, false);

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

    @Override
    public List<ClassRecord> getClassRecord(String teacherid, int pageNumber) {
        IPage<ClassRecord> page = new Page<>(pageNumber, 10); // 当前页1，每页10条
        LambdaUpdateWrapper<ClassRecord> queryWrapper = new LambdaUpdateWrapper<>();
        queryWrapper.eq(ClassRecord::getTeacherId, teacherid);
        IPage<ClassRecord> userPage = classRecordMapper.selectPage(page, queryWrapper);
        return (List<ClassRecord>) userPage.getRecords(); // 获取当前页数据
    }

    @Override
    public void insertClassRecord(String teacherid, ClassRecord classRecord) {

    }


    public void deleteVideo(String id) throws MyException {
        try {
            // 实例化一个认证对象，入参需要传入腾讯云账户secretId，secretKey,此处还需注意密钥对的保密
            // 密钥可前往https://console.cloud.tencent.com/cam/capi网站进行获取
            Credential cred = new Credential(yun.getId(), yun.getKey());
            // 实例化一个http选项，可选的，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("vod.tencentcloudapi.com");
            // 实例化一个client选项，可选的，没有特殊需求可以跳过
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            // 实例化要请求产品的client对象,clientProfile是可选的
            VodClient client = new VodClient(cred, "", clientProfile);
            // 实例化一个请求对象,每个接口都会对应一个request对象
            DeleteMediaRequest req = new DeleteMediaRequest();
            req.setFileId(id);
            // 返回的resp是一个DeleteMediaResponse的实例，与请求对象对应
            DeleteMediaResponse resp = client.DeleteMedia(req);
            // 输出json格式的字符串回包
            System.out.println(DeleteMediaResponse.toJsonString(resp));
        } catch (TencentCloudSDKException e) {
            throw new MyException("删除失败");
        }
    }

    public void record(String teacherid, String path) throws MyException {
        Process process = null;
        try {
            log.info("视频路径为：" + path);
            // 定义FFmpeg录制视频命令（包含音频）
            String ffmpegCommand = "ffmpeg -f v4l2 -video_size 640x480 -i /dev/video0 -f alsa -i default -c:v libx264 -c:a aac " + path;

            // 创建ProcessBuilder对象并执行FFmpeg命令
            ProcessBuilder pb = new ProcessBuilder("bash", "-c", ffmpegCommand);
            process = pb.start();
            while (map.get(teacherid)) {
                Thread.sleep(1000);
            }
            // 等待录制完成
            if (process != null) {
                process.destroy();
                log.info("FFmpeg进程已关闭");
            }
            log.info("视频录制完成");
            Thread.sleep(500);
        } catch (IOException e) {
            log.info("录制错误");
            throw new MyException("录制错误");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<String> upload(String path) throws MyException {
        VodUploadClient client = new VodUploadClient(yun.getId(), yun.getKey());
        VodUploadRequest request = new VodUploadRequest();
        request.setMediaFilePath(path);
        ArrayList<String> video = new ArrayList<>();
        try {
            VodUploadResponse response = client.upload("ap-guangzhou", request);
            video.add(response.getFileId());
            video.add(response.getMediaUrl());
            log.info("视频保存成功");
            log.info("视频ID为：" + video.get(0));
            log.info("视频路径为：" + video.get(1));
        } catch (Exception e) {
            // 业务方进行异常处理
            throw new MyException("保存错误");
        }
        return video;
    }

    public long request(String videourl) {
        //发送语言转文字请求
        long taskId = 0L;
        try {
            // 实例化一个认证对象，入参需要传入腾讯云账户 SecretId 和 SecretKey，此处还需注意密钥对的保密
            // 代码泄露可能会导致 SecretId 和 SecretKey 泄露，并威胁账号下所有资源的安全性。以下代码示例仅供参考，建议采用更安全的方式来使用密钥，请参见：https://cloud.tencent.com/document/product/1278/85305
            // 密钥可前往官网控制台 https://console.cloud.tencent.com/cam/capi 进行获取
            Credential cred = new Credential(yun.getId(), yun.getKey());
            // 实例化一个http选项，可选的，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("asr.tencentcloudapi.com");
            // 实例化一个client选项，可选的，没有特殊需求可以跳过
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            // 实例化要请求产品的client对象,clientProfile是可选的
            AsrClient client1 = new AsrClient(cred, "", clientProfile);
            // 实例化一个请求对象,每个接口都会对应一个request对象
            CreateRecTaskRequest req = new CreateRecTaskRequest();
            req.setEngineModelType("16k_zh_dialect");
            req.setChannelNum(1L);
            req.setResTextFormat(3L);
            req.setSourceType(0L);
            req.setUrl(videourl);
            // 返回的resp是一个CreateRecTaskResponse的实例，与请求对象对应
            CreateRecTaskResponse resp = client1.CreateRecTask(req);
            // 输出json格式的字符串回包
            log.info(CreateRecTaskResponse.toJsonString(resp));
            taskId = resp.getData().getTaskId();
            log.info("taskId为：" + taskId);

        } catch (TencentCloudSDKException e) {
            System.out.println(e.toString());
        }
        return taskId;
    }

    public void response(String classname, long taskId) {
        //获取字幕信息并且保存字幕文件
        try {
            Thread.sleep(10000);
            // 实例化一个认证对象，入参需要传入腾讯云账户 SecretId 和 SecretKey，此处还需注意密钥对的保密
            // 代码泄露可能会导致 SecretId 和 SecretKey 泄露，并威胁账号下所有资源的安全性。以下代码示例仅供参考，建议采用更安全的方式来使用密钥，请参见：https://cloud.tencent.com/document/product/1278/85305
            // 密钥可前往官网控制台 https://console.cloud.tencent.com/cam/capi 进行获取
            Credential cred = new Credential(yun.getId(), yun.getKey());
            // 实例化一个http选项，可选的，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("asr.tencentcloudapi.com");
            // 实例化一个client选项，可选的，没有特殊需求可以跳过
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            // 实例化要请求产品的client对象,clientProfile是可选的
            AsrClient client = new AsrClient(cred, "", clientProfile);
            // 实例化一个请求对象,每个接口都会对应一个request对象
            DescribeTaskStatusRequest req = new DescribeTaskStatusRequest();
            req.setTaskId(taskId);
            // 返回的resp是一个DescribeTaskStatusResponse的实例，与请求对象对应
            DescribeTaskStatusResponse resp = client.DescribeTaskStatus(req);
            // 输出json格式的字符串回包
            log.info(DescribeTaskStatusResponse.toJsonString(resp));
            String time = "";
            for (int i = 0; i < resp.getData().getResultDetail().length; i++) {
                time += String.valueOf(i + 1) + "\n";
                long l = resp.getData().getResultDetail()[i].getStartMs();
                long i1 = l / (1000 * 60 * 60);
                long i2 = l / (1000 * 60) % 60;
                long i3 = l / (1000) % 60;
                long i4 = l % 1000;
                if (i1 < 10) {
                    time += "0" + i1;
                } else {
                    time += String.valueOf(i1);
                }
                if (i2 < 10) {
                    time += ":0" + i2;
                } else {
                    time += ":" + String.valueOf(i2);
                }
                if (i3 < 10) {
                    time += ":0" + i3;
                } else {
                    time += ":" + String.valueOf(i3);
                }
                if (i4 < 10) {
                    time += ",00" + i4;
                } else if (i4 < 100) {
                    time += ",0" + String.valueOf(i4);
                } else {
                    time += "," + String.valueOf(i4);
                }
                time += " --> ";
                l = resp.getData().getResultDetail()[i].getEndMs();
                i1 = l / (1000 * 60 * 60);
                i2 = l / (1000 * 60) % 60;
                i3 = l / (1000) % 60;
                i4 = l % 1000;
                if (i1 < 10) {
                    time += "0" + i1;
                } else {
                    time += String.valueOf(i1);
                }
                if (i2 < 10) {
                    time += ":0" + i2;
                } else {
                    time += ":" + String.valueOf(i2);
                }
                if (i3 < 10) {
                    time += ":0" + i3;
                } else {
                    time += ":" + String.valueOf(i3);
                }
                if (i4 < 10) {
                    time += ",00" + i4;
                } else if (i4 < 100) {
                    time += ",0" + String.valueOf(i4);
                } else {
                    time += "," + String.valueOf(i4);
                }
                time += "\n";
                time += resp.getData().getResultDetail()[i].getFinalSentence() + "\n";
            }
            File file = new File("/opt/SmartClassRoom/11/" + classname + ".srt");
            try {
                FileWriter fw = new FileWriter(file);
                fw.write(time);
                fw.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (TencentCloudSDKException e) {
            System.out.println(e.toString());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void synthesis(String teacherid, String classname, String path) {
        String videoFile = path; // 视频文件路径
        String subtitlesFile = "/opt/SmartClassRoom/11/" + classname + ".srt"; // 字幕文件路径
        String outputVideoFile = "/opt/SmartClassRoom/11/" + classname + ".mp4"; // 输出视频文件路径
        String ffmpegCommand = "ffmpeg -i " + videoFile + " -vf subtitles=" + subtitlesFile + " " + outputVideoFile;

        try {
            Process process = Runtime.getRuntime().exec(ffmpegCommand);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            process.waitFor();
            log.info("字幕已成功添加到视频中！");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}
