package com.fox.client;

import com.fox.entity.Result;
import com.fox.entity.Student;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@FeignClient(name = "student-service")
public interface StudentClient {

    @GetMapping("/student/test")
    Result<String> test();

    @PostMapping("/student/register")
    Result<?> register(@RequestBody Student student);

    @PostMapping("/student/login")
    Result<?> login(
            @RequestParam("studentid") String studentid,
            @RequestParam("password") String password
    );

    @PostMapping("/student/{studentid}/change-password")
    Result<?> changePassword(
            @PathVariable("studentid") String studentid,
            @RequestBody Map<String, String> requestBody
    );

    @GetMapping("/student/{studentid}/info")
    Result<?> getStudentInfo(@PathVariable("studentid") String studentid);

    @PostMapping("/student/{studentid}/info")
    Result<?> postStudentInfo(
            @PathVariable("studentid") String studentid,
            @RequestBody Student requestBody
    );

    @PostMapping("/student/{studentid}/avatar")
    Result<?> avatarUpload(
            @PathVariable("studentid") String studentid,
            @RequestPart("file") MultipartFile file
    );
}