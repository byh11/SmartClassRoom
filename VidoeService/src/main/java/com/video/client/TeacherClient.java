package com.video.client;


import com.video.entity.Teacher;
import org.com.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;


@FeignClient(name = "teacher-service")
public interface TeacherClient {

    @GetMapping("/teacher/{teacherid}/info")
    @ResponseBody
    Result<Teacher> getTeacherInfo(@PathVariable String teacherid);

}
