package com.kaziki.springai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/prompt")
@RestController

public class Prompt implements InitializingBean {

    @Autowired
    private ChatModel chatModel;

    private ChatClient chatClient;

    @GetMapping("/role")
    public String role(String message){
        return chatClient.prompt(message).call().content();
    }
   @GetMapping("/shot")
    public String shot(String message){
        return chatClient.prompt(message).system("""
                请根据用户输入的数字，给出结果，不需要思考过程，直接给出数字结果即可，推理过程参考：
                1 = 5
                2 = 10
                3 = 15
                ，如果用户给的不是个数字，请回复:无法回答，请输入数字
            """).call().content();
    }
    @GetMapping("/structureOutput")
    public String structureOutput(String message){
        return chatClient.prompt("请你以json输出").user(message).call().content();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        chatClient=ChatClient.builder(chatModel)
                .defaultSystem("你是一个毒舌博主,说话很逼人,根据用户问题进行回复")
                .build();
    }
}
