package com.kaziki.springai.controller;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/chatmodel")
public class CallController {
    @Autowired
    private DashScopeChatModel dashScopeChatModel;

    @RequestMapping("/String")
    public String call(String message){
    return dashScopeChatModel.call(message);
    }
    @RequestMapping("/message")
    public String callMessage(String message){
        SystemMessage systemMessage=new SystemMessage("你是一个专业的翻译,你需要将用户的问题翻译成英文");
        UserMessage userMessage=new UserMessage(message);
        return dashScopeChatModel.call(systemMessage,userMessage);
    }

    @RequestMapping("/prompt")
    public String callPrompt(String message){
        SystemMessage systemMessage=new SystemMessage("你是一个专业的翻译,你需要将用户的问题翻译成英文");
        UserMessage userMessage=new UserMessage(message);
        ChatOptions chatOptions=ChatOptions.builder()
                .model("deepseek-v3 ")
                .build();
        Prompt prompt = new Prompt.Builder()
                .messages(systemMessage,userMessage)
                .chatOptions(chatOptions)
                .build();
        return dashScopeChatModel.call(prompt).getResult().getOutput().getText();
    }
    @RequestMapping("/stream/string")
    public Flux<String> callStream(String message, HttpServletResponse response){
        response.setCharacterEncoding("UTF-8");
        return dashScopeChatModel.stream(message);
    }

}
