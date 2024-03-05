import com.qcloud.vod.model.VodUploadResponse;
import com.tencentcloudapi.vod.v20180717.VodClient;
import com.tencentcloudapi.vod.v20180717.models.ApplyUploadRequest;
import com.tencentcloudapi.vod.v20180717.models.ApplyUploadResponse;
import com.tencentcloudapi.vod.v20180717.models.CommitUploadRequest;
import com.tencentcloudapi.vod.v20180717.models.CommitUploadResponse;
import org.bytedeco.javacv.*;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_videoio.VideoCapture;
import org.junit.jupiter.api.Test;

import javax.sound.sampled.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.asr.v20190614.AsrClient;
import com.tencentcloudapi.asr.v20190614.models.*;

public class test {

    @Test
    public void aaa() {
        Process process = null;

        try {
            // 定义FFmpeg录制视频命令（包含音频）
            String ffmpegCommand = "ffmpeg -f v4l2 -video_size 640x480 -i /dev/video0 -f alsa -i default -c:v libx264 -c:a aac output.mp3";

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
        } finally {

        }


    }


    @Test
    public void bbb() {
        try{
            // 实例化一个认证对象，入参需要传入腾讯云账户 SecretId 和 SecretKey，此处还需注意密钥对的保密
            // 代码泄露可能会导致 SecretId 和 SecretKey 泄露，并威胁账号下所有资源的安全性。以下代码示例仅供参考，建议采用更安全的方式来使用密钥，请参见：https://cloud.tencent.com/document/product/1278/85305
            // 密钥可前往官网控制台 https://console.cloud.tencent.com/cam/capi 进行获取
            Credential cred = new Credential("", "");
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
            Credential cred = new Credential("AKIDeBd3Qju57fKxQ0QjyFXxHTXZVN92nPnx", "IXbT8gCe8SnMxKj2EG9sP1b8c1Wt6C7u");
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
            req.setTaskId(8457750282L);
            // 返回的resp是一个DescribeTaskStatusResponse的实例，与请求对象对应
            DescribeTaskStatusResponse resp = client.DescribeTaskStatus(req);
            // 输出json格式的字符串回包
            System.out.println(DescribeTaskStatusResponse.toJsonString(resp));
            System.out.println(resp.getData().getResult());
        } catch (TencentCloudSDKException e) {
            System.out.println(e.toString());
        }
    }

//    @Test
//    public void ddd(){
//        String secretId = "";
//        String secretKey = "";
//
//        // 视频 ID
//        String fileId = "1397757886374820067";
//
//        /// 创建 VOD 客户端
//        VodClient client = new VodClient();
//
//        // 设置要添加字幕的视频 ID
//        String videoId = "your_video_id";
//
//        // 设置要添加的字幕文件路径
//        String subtitleFilePath = "path/to/your/subtitle.vtt";
//
//        // 调用腾讯云 VOD API 添加字幕
//        try {
//            client.addSubtitleToVideo(videoId, subtitleFilePath);
//            System.out.println("Subtitle added successfully.");
//        } catch (Exception e) {
//            System.err.println("Error adding subtitle: " + e.getMessage());
//        }
//
//    }

}

