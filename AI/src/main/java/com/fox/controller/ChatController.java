package com.fox.controller;

import dev.langchain4j.community.model.dashscope.QwenChatModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/ai")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class ChatController {

    @Autowired
    QwenChatModel chatModel;

    @GetMapping("/chat")
    public Flux<String> chat(@RequestParam(value = "input") String input, @RequestParam(value = "user") String user) {
        log.info("user:{}", user);
        log.info("input:{}", input);
        return this.chatModel.prompt()
                .user(input)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, user)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .stream()
                .content();
    }
} 