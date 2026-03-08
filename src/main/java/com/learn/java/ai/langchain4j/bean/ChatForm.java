package com.learn.java.ai.langchain4j.bean;

import lombok.Data;

@Data
public class ChatForm {
    private long memoryId; // 对话id

    private String message; //用户问题
}
