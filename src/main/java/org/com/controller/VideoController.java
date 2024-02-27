package org.com.controller;

import org.com.entity.Result;
import org.com.entity.Video;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/video")
public class VideoController {
    @RequestMapping(value = "/selectVideo", method = RequestMethod.POST)
    @ResponseBody
    public Result<String> Register(String  videoName){
        return Result.success();
    }
}
