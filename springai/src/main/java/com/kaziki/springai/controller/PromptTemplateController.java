package com.kaziki.springai.controller;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.HashMap;

@RequestMapping("/template")
@RestController
public class PromptTemplateController implements InitializingBean {

    @Autowired
    private ChatModel chatModel;

    private ChatClient chatClient;
    @Value("classpath:/templates/open_source_system_prompt.st")
    private Resource template;
    @GetMapping("/call")
    public String call(String topic){
        String template= """
                请给我推荐几个关于{topic}的开源项目""";
        PromptTemplate promptTemplate=new PromptTemplate(template);
        promptTemplate.add("topic",topic);
        /*
        * return chatClient.prompt(new PromptTemplate(temple).create(Map.of("topic",topic))).call().content(
        * */
        return chatClient.prompt(promptTemplate.create()).call().content();
    }

    @GetMapping("/file")
    public Flux<String> file(@RequestParam("topic") String topic, HttpServletResponse response ){
        response.setCharacterEncoding("UTF-8");
        HashMap variables=new HashMap();
        variables.put("topic",topic);
        variables.put("language","Java");
        PromptTemplate promptTemplate=PromptTemplate.builder().resource(template).variables(variables).build();
        /*
         * return chatClient.prompt(new PromptTemplate(temple).create(Map.of("topic",topic))).call().content(
         * */
        return chatClient.prompt(promptTemplate.create()).stream().content();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        chatClient=ChatClient.builder(chatModel)
                .defaultOptions(DashScopeChatOptions.builder().temperature(0.7).build())
                .build();
    }
}
