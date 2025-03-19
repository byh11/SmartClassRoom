package com.video.service.service;


import com.video.entity.Video;
import org.com.execption.MyException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface VideoService {
    void uploadVideo(MultipartFile file, String teacherid) throws MyException;

    List<Video> selectVideo(String videoName) throws MyException;

    List<Video> selectVideoPage(int pageSize, int pageNumber) throws MyException;

    void deleteVideo(String id) throws MyException;

    void downLoadVideo(String id);

    void saveVideo(Video video);

    void live(String teacherid) throws MyException;

    void stopLive(String teacherid) throws MyException;
}
