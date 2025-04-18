package com.fox.config;

import com.fox.service.ToolsService;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.service.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    @Bean
    public AssistantUnique assistantUnique(ChatLanguageModel qwenChatModel,
                                           StreamingChatLanguageModel qwenStreamingChatModel,
                                           ToolsService toolsService) {

        AssistantUnique assistant = AiServices.builder(AssistantUnique.class)
                .tools(toolsService)
                .chatLanguageModel(qwenChatModel)
                .streamingChatLanguageModel(qwenStreamingChatModel)
                .chatMemoryProvider(memoryId ->
                        MessageWindowChatMemory.builder().maxMessages(10)
                                .id(memoryId).build()
                )
                .build();


        return assistant;
    }

    @Bean
    public AssistantUnique assistantUniqueStore(ChatLanguageModel qwenChatModel,
                                                StreamingChatLanguageModel qwenStreamingChatModel) {

        PersistentChatMemoryStore store = new PersistentChatMemoryStore();

        ChatMemoryProvider chatMemoryProvider = memoryId -> MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(10)
                .chatMemoryStore(store)
                .build();

        AssistantUnique assistant = AiServices.builder(AssistantUnique.class)
                .chatLanguageModel(qwenChatModel)
                .streamingChatLanguageModel(qwenStreamingChatModel)
                .chatMemoryProvider(chatMemoryProvider)
                .build();
        return assistant;
    }


    public interface AssistantUnique {

        String chat(@MemoryId int memoryId, @UserMessage String userMessage);

        // 流式响应
        @SystemMessage("你是一个智慧学习网站的智能系统，本系统拥有教师和学生两种角色，如果用户需要查询角色及其任何信息，你要先去获取角色的基础信息。拿到基础信息后再去执行其他处理，" +
                "以保证在其他处理时能获取到需要的信息进行处理。请在返回视频时，只要有关视频的查询就要输出视频名称和url，不要输出视频的id，将url放置在视频名称上去。" +
                "请正确组装视频地址，格式为：http://localhost:3000/video/视频id，如果我在查询相关视频的时候没有满足查询数量，请你自行在网络上寻找相关视频并且以站外推荐形式展示。" +
                "在输出所有数据时请格式化为Markdown格式进行输出")
        TokenStream stream(@MemoryId String memoryId, @UserMessage String userMessage);
    }


}