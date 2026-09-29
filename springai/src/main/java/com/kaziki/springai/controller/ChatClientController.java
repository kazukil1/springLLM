package com.kaziki.springai.controller;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/client")
public class ChatClientController implements InitializingBean {

    @Autowired
    private ChatModel chatModel;

    private ChatClient chatClient;
    @Override
    public void afterPropertiesSet() throws Exception {
        chatClient =ChatClient.builder(chatModel)
                .defaultAdvisors(new SimpleLoggerAdvisor()
                ).defaultSystem("1+1")
                .defaultOptions(
                        DashScopeChatOptions.builder().temperature(0.7)
                                .build()
                ).build();
    }
    //提示词覆盖了系统消息
    @GetMapping("/callstring")
    public String callstring(String message){
        return chatClient.prompt(message).system("再加3").call().content();
    }
    //不会覆盖系统提示词
    @GetMapping("/call")
    public String call(String message){
        return chatClient.prompt(new Prompt(new SystemMessage("再加3"),new UserMessage(message))).call().content();
    }
    //流式返回
    @GetMapping("/stream")
    public Flux<String> stream(String message){
        return chatClient.prompt(new Prompt(new SystemMessage("再加3"),new UserMessage(message))).stream().content();
    }
}