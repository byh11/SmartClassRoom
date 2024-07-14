package com.video.service.service;


import com.video.entity.Video;
import org.com.execption.MyException;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;

public interface VideoService {
    void UploadVideo(MultipartFile file,String teacherid) throws MyException;

    ArrayList<Video> SelectVideo(String videoName) throws MyException;

    void DeleteVideo(String id) throws MyException;

    void DownLoadVideo(String id);

    void SaveVideo(Video video);

    void Live(String teacherid) throws MyException;

    void StopLive(String teacherid) throws MyException;
}
