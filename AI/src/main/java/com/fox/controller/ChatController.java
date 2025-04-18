package com.fox.controller;

import com.fox.config.AiConfig;
import dev.langchain4j.service.TokenStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
@RequestMapping("/ai")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class ChatController {

    @Autowired
    AiConfig.AssistantUnique assistantUnique;

    @GetMapping("/chat")
    public Flux<String> chat(@RequestParam(value = "input") String input, @RequestParam(value = "user") String user) {
        log.info("user:{}", user);
        log.info("input:{}", input);
        TokenStream stream = assistantUnique.stream(user, input);

        return Flux.create(sink -> {
            stream.onPartialResponse(s -> sink.next(s))
                    .onCompleteResponse(c -> sink.complete())
                    .onError(sink::error)
                    .start();

        });
    }
} 