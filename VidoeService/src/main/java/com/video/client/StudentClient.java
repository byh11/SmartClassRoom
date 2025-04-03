package com.video.client;


import com.video.entity.Student;
import org.com.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;


@FeignClient(name = "student-service")
public interface StudentClient {

    @GetMapping("/student/{studentid}/info")
    @ResponseBody
    Result<Student> getStudentInfo(@PathVariable String studentid);

}
