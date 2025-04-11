package com.fox.service;

import com.fox.client.TeacherClient;
import com.fox.entity.Result;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TeacherService {

    @Autowired
    private TeacherClient teacherClient;

    @Tool
    public Result<?> getTeacherInfo(String teacherid) {
        return teacherClient.getTeacherInfo(teacherid);
    }
}
