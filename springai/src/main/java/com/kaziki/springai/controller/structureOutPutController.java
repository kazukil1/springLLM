package com.kaziki.springai.controller;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.kaziki.springai.model.Book;
import com.kaziki.springai.model.Book2;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/structure")
public class structureOutPutController implements InitializingBean {
    @Autowired
    private ChatModel chatModel;

    private ChatClient chatClient;
    /**
     * 调用
     * 用BeanOutputConverter
     */
    @GetMapping("/call")
    public String call(@RequestParam String message, HttpServletResponse response){
        response.setCharacterEncoding("UTF-8");

        PromptTemplate promptTemplate=PromptTemplate.builder().template("请给我推荐几本java有关的书,输出格式为:{format}").build();
        BeanOutputConverter converter=new BeanOutputConverter(Book2.class);
        String JsonStr=chatClient.prompt(promptTemplate.create(Map.of("format",converter.getFormat())))
                .call().content();
        System.out.println("JsonStr:"+JsonStr);
        Book2 book2= (Book2) converter.convert(JsonStr);
        return book2.toString();
    }

    @GetMapping("/convert")
    public String convert(@RequestParam String message, HttpServletResponse response){
        response.setCharacterEncoding("UTF-8");
        Book book=chatClient.prompt("请给我推荐几本心理学的书,结果用中文").call().entity(Book.class);

        return book.toString();
    }
    @GetMapping("/convertList")
    public String convertList(@RequestParam String message, HttpServletResponse response){
        response.setCharacterEncoding("UTF-8");
        List<Book> book=chatClient.prompt("请给我推荐几本心理学的书,结果用中文").call().entity(new ParameterizedTypeReference<List<Book>>() {
        });
        return book.toString();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        chatClient=ChatClient.builder(chatModel)
                //实现logger的advisor
                .defaultAdvisors(new SimpleLoggerAdvisor())
                //设置模型参数
                .defaultOptions(DashScopeChatOptions.builder().temperature(0.7).build())
                .build();
    }
}
