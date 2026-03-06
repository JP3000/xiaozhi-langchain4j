package com.learn.java.ai.langchain4j;

import com.learn.java.ai.langchain4j.assistant.Assistant;
import com.learn.java.ai.langchain4j.assistant.MemoryChatAssistant;
import com.learn.java.ai.langchain4j.config.MemoryChatAssistantConfig;
import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.spring.AiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AIServiceTest {

    @Autowired
    private QwenChatModel qwenChatModel;
    @Autowired
    private Assistant assistant;

    @Test
    public void testCHat(){
        // 创建AISerivce
        Assistant assistant = AiServices.create(Assistant.class, qwenChatModel);

        //调用service的接口
        String answer = assistant.chat("Hello");
        System.out.println(answer);
    }

    @Test
    public void testAssistant(){
        String answer = assistant.chat("Who are you?");
        System.out.println(answer);
    }

}
