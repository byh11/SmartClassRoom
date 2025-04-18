package com.video.service.service;


import com.video.entity.Video;
import org.com.execption.MyException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface VideoService {
    void uploadVideo(MultipartFile file, String teacherid, String videoName, String videoText, String videoType, String duration) throws MyException;

    List<?> selectVideo(String videoName) throws MyException;

    List<?> selectVideoByTeacher(String teacherid) throws MyException;

    List<?> selectVideoPage(int pageSize, int pageNumber) throws MyException;

    void deleteVideo(String id) throws MyException;

    void saveVideo(Video video);

    void live(String teacherid) throws MyException;

    void stopLive(String teacherid) throws MyException;

    Video selectVideoById(String id) throws MyException;

    String getVideoDownloadUrl(String id) throws MyException;

    boolean update(String id, Video video) throws MyException;

    void play(String videoid) throws MyException;

    long likeVideo(String userid, String videoid, int userType) throws MyException;

    long unlikeVideo(String userid, String videoid, int userType) throws MyException;

    long collectVideo(String userid, String videoid, int userType) throws MyException;

    long uncollectVideo(String userid, String videoid, int userType) throws MyException;

    List<?> getComments(String videoid) throws MyException;

    void addComment(String userid, String videoid, String content, int userType) throws MyException;

    void deleteComment(String commentid) throws MyException;

    void addReply(String userid, String videoid, String content, long parentId, int userType) throws MyException;

    Map<?, ?> getVideoStatus(String userid, String videoid, int userType) throws MyException;

    List<?> getVideoTopByField(int pageNumber, int pageSize, String field, String order) throws MyException;

    List<?> getVideoByName(int pageNumber, int pageSize, String name) throws MyException;

    List<?> getVideoByType(int pageNumber, int pageSize, String type) throws MyException;

    List<?> getLiked(String userid, int userType) throws MyException;

    List<?> getCollected(String userid, int userType) throws MyException;

}
