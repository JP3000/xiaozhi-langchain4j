package com.learn.java.ai.langchain4j.config;

import com.learn.java.ai.langchain4j.store.MongoChatMemoryStore;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class XiaozhiAgentConfig {

    @Autowired
    // 利用mongo的持久化存储作为小智的持久化信息的存储位置
    private MongoChatMemoryStore mongoChatMemoryStore;

    @Bean
    public ChatMemoryProvider chatMemoryProviderXiaozhi() {

        return memoryId ->
            MessageWindowChatMemory.builder()
                    .id(memoryId)
                    .maxMessages(20)//来回一轮，2条信息，20则是10轮对话的记忆
                    .chatMemoryStore(mongoChatMemoryStore)
                    .build();

    }

}
