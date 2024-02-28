import com.qcloud.vod.VodUploadClient;
import com.qcloud.vod.model.VodUploadRequest;
import com.qcloud.vod.model.VodUploadResponse;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.vod.v20180717.VodClient;
import com.tencentcloudapi.vod.v20180717.models.DeleteMediaRequest;
import com.tencentcloudapi.vod.v20180717.models.DeleteMediaResponse;
import org.com.mapper.Redis;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class test {
    @Test
    public void main() {
//        VodUploadClient client = new VodUploadClient("AKIDeBd3Qju57fKxQ0QjyFXxHTXZVN92nPnx", "IXbT8gCe8SnMxKj2EG9sP1b8c1Wt6C7u");
//        VodUploadRequest request = new VodUploadRequest();
//        request.setMediaFilePath("/opt/SmartClassRoom/11测试视频.mp4");
//
//        try {
//            VodUploadResponse response = client.upload("ap-guangzhou", request);
//            System.out.println(response.getFileId());
//            System.out.println(response.getMediaUrl());
////            logger.info("Upload FileId = {}", response.getFileId());
//        } catch (Exception e) {
//            // 业务方进行异常处理
////            logger.error("Upload Err", e);
//        }
//        try {
//            // 实例化一个认证对象，入参需要传入腾讯云账户secretId，secretKey,此处还需注意密钥对的保密
//            // 密钥可前往https://console.cloud.tencent.com/cam/capi网站进行获取
//            Credential cred = new Credential("AKIDeBd3Qju57fKxQ0QjyFXxHTXZVN92nPnx", "IXbT8gCe8SnMxKj2EG9sP1b8c1Wt6C7u");
//            // 实例化一个http选项，可选的，没有特殊需求可以跳过
//            HttpProfile httpProfile = new HttpProfile();
//            httpProfile.setEndpoint("vod.tencentcloudapi.com");
//            // 实例化一个client选项，可选的，没有特殊需求可以跳过
//            ClientProfile clientProfile = new ClientProfile();
//            clientProfile.setHttpProfile(httpProfile);
//            // 实例化要请求产品的client对象,clientProfile是可选的
//            VodClient client = new VodClient(cred, "", clientProfile);
//            // 实例化一个请求对象,每个接口都会对应一个request对象
//            DeleteMediaRequest req = new DeleteMediaRequest();
//            req.setFileId("1397757885839022848");
//            // 返回的resp是一个DeleteMediaResponse的实例，与请求对象对应
//            DeleteMediaResponse resp = client.DeleteMedia(req);
//            // 输出json格式的字符串回包
//            System.out.println(DeleteMediaResponse.toJsonString(resp));
////        } catch (TencentCloudSDKException e) {
////            System.out.println(e.toString());
////            throw new GgktException(20001,"删除视频失败");
////        }
//        } catch (TencentCloudSDKException e) {
//            throw new RuntimeException(e);
//        }
//        Redis redis = new Redis();
//        redis.getKey("测试");

        String url = "https://1324618242.vod-qcloud.com/9e20a3e3vodcq1324618242/1dbb81d31397757885846414502/cYFnSNecadUA.mp4";
        String fileName = "/opt/SmartClassRoom/11/123.mp4";

        try {
            URL downloadUrl = new URL(url);
            URLConnection connection = downloadUrl.openConnection();
            InputStream inputStream = connection.getInputStream();

            FileOutputStream outputStream = new FileOutputStream(fileName);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();
            System.out.println("File downloaded successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
