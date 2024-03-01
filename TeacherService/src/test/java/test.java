import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_videoio.VideoCapture;
import org.junit.jupiter.api.Test;

import javax.sound.sampled.*;
import java.io.IOException;
import java.util.Scanner;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Scanner;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
public class test {

    AudioFormat audioFormat=new AudioFormat(16000F, 16, 1,true,false);
    /**目标数据线是可DataLine读取音频数据的类型DataLine 。
     TargetDataLine接口提供了从目标数据线缓冲区读取捕获数据的方法。*/
    TargetDataLine targetDataLine;
    SourceDataLine sourceDataLine;
    byte[] bytes = new byte[1280];
    String recordFileStorePath="/opt/SmartClassRoom/TeacherService";
    @Test
    public void aaa() {
//        VideoCapture camera = new VideoCapture(0);
//        Mat frame = new Mat();
//        if (camera.isOpened()) {
//            while (true) {
//                camera.read(frame);
//                // 在这里处理每一帧图像
//            }
//        } else {
//            System.out.println("无法打开摄像头");
//        }

        System.out.println("y开始录音,n结束录音");
        Scanner scanner = new Scanner(System.in);
        String startCmd = scanner.next();
        long startTime = System.currentTimeMillis();
        if(startCmd.equals("y")){
            System.out.println("正在录音...");
            RecordThread recordThread=new RecordThread();
            recordThread.start();
            Scanner scannerAnother = new Scanner(System.in);
            String endCmd = scannerAnother.next();
            if(endCmd.equals("n")){
                System.out.println("录音关闭...");
                System.out.println("共录音了"+(System.currentTimeMillis()-startTime)/1000+"秒！");
                //这里必须停止关闭,否则导致无法正常播放
                sourceDataLine.drain();
                sourceDataLine.close();
                targetDataLine.stop();
                targetDataLine.close();
                System.exit(0);
            }
        }

    }


    public class RecordThread extends Thread{
        public void run(){
            int len = -1;
            DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
            try {
                targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
                //开启录音线程
                targetDataLine.open(audioFormat);
                targetDataLine.start();
                long startTime=System.currentTimeMillis();
                // 设置数据输入,开启播放监听
                DataLine.Info dataLineInfoOut = new DataLine.Info(SourceDataLine.class,audioFormat, AudioSystem.NOT_SPECIFIED);
                sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfoOut);
                sourceDataLine.open(audioFormat);
                sourceDataLine.start();
                File file = new File(recordFileStorePath);
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
                while ((len = new AudioInputStream(targetDataLine).read(bytes)) != -1) {
                    fileOutputStream.write(bytes= Arrays.copyOfRange(bytes, 0, len),0, len);
                    sourceDataLine.write( Arrays.copyOfRange(bytes, 0, len), 0, len);
                    long endTime=System.currentTimeMillis();
                    if (endTime-startTime>60000) {
                        //关闭实时播放流
                        sourceDataLine.drain();
                        sourceDataLine.close();
                        targetDataLine.stop();
                        targetDataLine.close();
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

