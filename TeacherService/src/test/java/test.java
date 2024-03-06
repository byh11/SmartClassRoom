import com.google.gson.Gson;
import com.qcloud.vod.VodUploadClient;
import com.qcloud.vod.model.VodUploadRequest;
import com.qcloud.vod.model.VodUploadResponse;
import com.tencentcloudapi.vod.v20180717.VodClient;
import com.tencentcloudapi.vod.v20180717.models.ApplyUploadRequest;
import com.tencentcloudapi.vod.v20180717.models.ApplyUploadResponse;
import com.tencentcloudapi.vod.v20180717.models.CommitUploadRequest;
import com.tencentcloudapi.vod.v20180717.models.CommitUploadResponse;
import org.bytedeco.javacv.*;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_videoio.VideoCapture;
import org.com.execption.MyException;
import org.junit.jupiter.api.Test;

import javax.sound.sampled.*;
import java.io.*;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.asr.v20190614.AsrClient;
import com.tencentcloudapi.asr.v20190614.models.*;
import org.opencv.video.Video;
import org.springframework.beans.factory.annotation.Autowired;

public class test {


    private Gson gson=new Gson();

    String id="";
    String key="";

    @Test
    public void aaa() {
        Process process = null;
        try {
            // 定义FFmpeg录制视频命令（包含音频）
            String ffmpegCommand = "ffmpeg -f v4l2 -video_size 640x480 -i /dev/video0 -f alsa -i default -c:v libx264 -c:a aac output.mp4";

            // 创建ProcessBuilder对象并执行FFmpeg命令
            ProcessBuilder pb = new ProcessBuilder("bash", "-c", ffmpegCommand);
            process = pb.start();
            Thread.sleep(5000);
            // 等待录制完成
            if (process != null) {
                process.destroy();
                System.out.println("FFmpeg进程已关闭");
            }
            System.out.println("视频录制完成");
            Thread.sleep(1000);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }


    @Test
    public void bbb() {
        try{
            // 实例化一个认证对象，入参需要传入腾讯云账户 SecretId 和 SecretKey，此处还需注意密钥对的保密
            // 代码泄露可能会导致 SecretId 和 SecretKey 泄露，并威胁账号下所有资源的安全性。以下代码示例仅供参考，建议采用更安全的方式来使用密钥，请参见：https://cloud.tencent.com/document/product/1278/85305
            // 密钥可前往官网控制台 https://console.cloud.tencent.com/cam/capi 进行获取
            Credential cred = new Credential(id, key);
            // 实例化一个http选项，可选的，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("asr.tencentcloudapi.com");
            // 实例化一个client选项，可选的，没有特殊需求可以跳过
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            // 实例化要请求产品的client对象,clientProfile是可选的
            AsrClient client = new AsrClient(cred, "", clientProfile);
            // 实例化一个请求对象,每个接口都会对应一个request对象
            CreateRecTaskRequest req = new CreateRecTaskRequest();
            req.setEngineModelType("16k_zh_dialect");
            req.setChannelNum(1L);
            req.setResTextFormat(3L);
            req.setSourceType(0L);
            req.setUrl("https://1324618242.vod-qcloud.com/fd141aaavodsh1324618242/0f8d58171397757886374820067/AN9UfdDmLFAA.mp4");
            // 返回的resp是一个CreateRecTaskResponse的实例，与请求对象对应
            CreateRecTaskResponse resp = client.CreateRecTask(req);
            // 输出json格式的字符串回包
            System.out.println(CreateRecTaskResponse.toJsonString(resp));
            System.out.println(resp.getData().getTaskId());
        } catch (TencentCloudSDKException e) {
            System.out.println(e.toString());
        }
    }


    @Test
    public void ccc(){
        try{
            // 实例化一个认证对象，入参需要传入腾讯云账户 SecretId 和 SecretKey，此处还需注意密钥对的保密
            // 代码泄露可能会导致 SecretId 和 SecretKey 泄露，并威胁账号下所有资源的安全性。以下代码示例仅供参考，建议采用更安全的方式来使用密钥，请参见：https://cloud.tencent.com/document/product/1278/85305
            // 密钥可前往官网控制台 https://console.cloud.tencent.com/cam/capi 进行获取
            Credential cred = new Credential(id, key);
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
            long a = 8469104580L;
            System.out.println(a);
            req.setTaskId(a);
            // 返回的resp是一个DescribeTaskStatusResponse的实例，与请求对象对应
            DescribeTaskStatusResponse resp = client.DescribeTaskStatus(req);
            // 输出json格式的字符串回包
            System.out.println(DescribeTaskStatusResponse.toJsonString(resp));
            System.out.println(resp.getData().getResult());
            for (int i = 0; i < resp.getData().getResultDetail().length; i++) {
                System.out.println(resp.getData().getResultDetail()[i].getFinalSentence());
                System.out.println(resp.getData().getResultDetail()[i].getStartMs());
                long l = resp.getData().getResultDetail()[i].getStartMs();
                System.out.println(resp.getData().getResultDetail()[i].getEndMs());
            }
        } catch (TencentCloudSDKException e) {
            System.out.println(e.toString());
        }
    }

    @Test
    public void lll(){
        long l = 4371259;
        long i1 = l/(1000*60*60);
        long i2 = l/(1000*60)%60;
        long i3 = l/(1000)%60;
        long i4 = l%1000;
        String time;
        if (i1<10){
            time="0"+i1;
        }else {
            time= String.valueOf(i1);
        }
        if (i2<10){
            time+=":0"+i2;
        }else {
            time+=":"+String.valueOf(i2);
        }
        if (i3<10){
            time+=":0"+i3;
        }else {
            time+=":"+String.valueOf(i3);
        }
        if (i4<10){
            time+=",00"+i4;
        }else if (i4<100){
            time+=":0"+String.valueOf(i4);
        }else {
            time+=":"+String.valueOf(i4);
        }
        time+=" --> ";
        time+="01:12:51:259";
        System.out.println(1);
        System.out.println(time);
        System.out.println("字幕");
    }

    @Test
    public void ddd(){
        String videoFile = "output.mp4"; // 视频文件路径
        String subtitlesFile = "a.srt"; // 字幕文件路径
        String outputVideoFile = "aaaa.mp4"; // 输出视频文件路径

        String ffmpegCommand = "ffmpeg -i " + videoFile + " -vf subtitles=" + subtitlesFile + " " + outputVideoFile;

        try {
            Process process = Runtime.getRuntime().exec(ffmpegCommand);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            process.waitFor();
            System.out.println("字幕已成功添加到视频中！");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void qqq(){
        String videoid = null;
        String videourl = null;
        long taskid = 0;
        //录制视频
//        Process process = null;
//        try {
//            // 定义FFmpeg录制视频命令（包含音频）
//            String ffmpegCommand = "ffmpeg -f v4l2 -video_size 640x480 -i /dev/video0 -f alsa -i default -c:v libx264 -c:a aac output.mp4";
//
//            // 创建ProcessBuilder对象并执行FFmpeg命令
//            ProcessBuilder pb = new ProcessBuilder("bash", "-c", ffmpegCommand);
//            process = pb.start();
//            Thread.sleep(10000);
//            // 等待录制完成
//            if (process != null) {
//                process.destroy();
//                System.out.println("FFmpeg进程已关闭");
//            }
//            System.out.println("视频录制完成");
//            Thread.sleep(1000);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }



        //上传视频
        VodUploadClient client = new VodUploadClient(id, key);
        VodUploadRequest request = new VodUploadRequest();
        request.setMediaFilePath("output.mp4");
        try {
            VodUploadResponse response = client.upload("ap-guangzhou", request);
            System.out.println(response.getFileId());
            System.out.println(response.getMediaUrl());
            videoid = response.getFileId();
            videourl=response.getMediaUrl();
        } catch (Exception e) {
            // 业务方进行异常处理
            System.out.println("保存错误");
        }


        //发送语言转文字请求
        try{
            // 实例化一个认证对象，入参需要传入腾讯云账户 SecretId 和 SecretKey，此处还需注意密钥对的保密
            // 代码泄露可能会导致 SecretId 和 SecretKey 泄露，并威胁账号下所有资源的安全性。以下代码示例仅供参考，建议采用更安全的方式来使用密钥，请参见：https://cloud.tencent.com/document/product/1278/85305
            // 密钥可前往官网控制台 https://console.cloud.tencent.com/cam/capi 进行获取
            Credential cred = new Credential(id, key);
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
            System.out.println(CreateRecTaskResponse.toJsonString(resp));
            System.out.println(resp.getData().getTaskId());
            taskid=resp.getData().getTaskId();
            System.out.println(taskid);

        } catch (TencentCloudSDKException e) {
            System.out.println(e.toString());
        }


        //获取字幕信息并且保存字幕文件
        try{
            // 实例化一个认证对象，入参需要传入腾讯云账户 SecretId 和 SecretKey，此处还需注意密钥对的保密
            // 代码泄露可能会导致 SecretId 和 SecretKey 泄露，并威胁账号下所有资源的安全性。以下代码示例仅供参考，建议采用更安全的方式来使用密钥，请参见：https://cloud.tencent.com/document/product/1278/85305
            // 密钥可前往官网控制台 https://console.cloud.tencent.com/cam/capi 进行获取
            Credential cred = new Credential(id, key);
            // 实例化一个http选项，可选的，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("asr.tencentcloudapi.com");
            // 实例化一个client选项，可选的，没有特殊需求可以跳过
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            // 实例化要请求产品的client对象,clientProfile是可选的
            AsrClient client2 = new AsrClient(cred, "", clientProfile);
            // 实例化一个请求对象,每个接口都会对应一个request对象
            DescribeTaskStatusRequest req = new DescribeTaskStatusRequest();
            req.setTaskId(taskid);
            // 返回的resp是一个DescribeTaskStatusResponse的实例，与请求对象对应
            DescribeTaskStatusResponse resp = client2.DescribeTaskStatus(req);
            // 输出json格式的字符串回包
            System.out.println("===============================");
            System.out.println(DescribeTaskStatusResponse.toJsonString(resp));
            System.out.println("===============================");
            System.out.println(resp.getData().getResult());
            String time="";
            for (int i = 0; i < resp.getData().getResultDetail().length; i++) {
                time+=String.valueOf(i+1)+"\n";
                long l=resp.getData().getResultDetail()[i].getStartMs();
                long i1 = l/(1000*60*60);
                long i2 = l/(1000*60)%60;
                long i3 = l/(1000)%60;
                long i4 = l%1000;
                if (i1<10){
                    time+="0"+i1;
                }else {
                    time+= String.valueOf(i1);
                }
                if (i2<10){
                    time+=":0"+i2;
                }else {
                    time+=":"+String.valueOf(i2);
                }
                if (i3<10){
                    time+=":0"+i3;
                }else {
                    time+=":"+String.valueOf(i3);
                }
                if (i4<10){
                    time+=",00"+i4;
                }else if (i4<100){
                    time+=",0"+String.valueOf(i4);
                }else {
                    time+=","+String.valueOf(i4);
                }
                time+=" --> ";
                l=resp.getData().getResultDetail()[i].getEndMs();
                i1 = l/(1000*60*60);
                i2 = l/(1000*60)%60;
                i3 = l/(1000)%60;
                i4 = l%1000;
                if (i1<10){
                    time+="0"+i1;
                }else {
                    time+=String.valueOf(i1);
                }
                if (i2<10){
                    time+=":0"+i2;
                }else {
                    time+=":"+String.valueOf(i2);
                }
                if (i3<10){
                    time+=":0"+i3;
                }else {
                    time+=":"+String.valueOf(i3);
                }
                if (i4<10){
                    time+=",00"+i4;
                }else if (i4<100){
                    time+=",0"+String.valueOf(i4);
                }else {
                    time+=","+String.valueOf(i4);
                }
                time+="\n";
                time+=resp.getData().getResultDetail()[i].getFinalSentence()+"\n";
            }
            System.out.println("\n\n");
            System.out.println(time);
            File file = new File("aaa.srt");
            try {
                FileWriter fw = new FileWriter(file);
                fw.write(time);
                fw.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (TencentCloudSDKException e) {
            System.out.println(e.toString());
        }


        String videoFile = "output.mp4"; // 视频文件路径
        String subtitlesFile = "aaa.srt"; // 字幕文件路径
        String outputVideoFile = "aaaa.mp4"; // 输出视频文件路径

        String ffmpegCommand = "ffmpeg -i " + videoFile + " -vf subtitles=" + subtitlesFile + " " + outputVideoFile;

        try {
            Process process1 = Runtime.getRuntime().exec(ffmpegCommand);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process1.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            process1.waitFor();
            System.out.println("字幕已成功添加到视频中！");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }


    }
}

