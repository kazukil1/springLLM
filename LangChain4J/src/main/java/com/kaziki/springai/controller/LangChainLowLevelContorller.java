package com.kaziki.springai.controller;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

import static dev.langchain4j.data.message.SystemMessage.systemMessage;
import static dev.langchain4j.data.message.UserMessage.userMessage;

@RequestMapping("/langchain/low")
@RestController
public class LangChainLowLevelContorller {
    @Autowired
    private OpenAiChatModel chatModel;
    @GetMapping("/hello")
    public String hello(){
        return chatModel.chat("你好,你是谁");
    }
    @Autowired
    private OpenAiStreamingChatModel streamingChatModel;
    @GetMapping("/streamhello")
    public void helloStream(HttpServletResponse response){
        response.setCharacterEncoding("UTF-8");
        streamingChatModel.chat("你是谁", new StreamingChatResponseHandler() {

            @Override
            public void onPartialResponse(String partialResponse) {
                System.out.println(partialResponse);
            }

            @Override
            public void onCompleteResponse(ChatResponse completeResponse) {
                System.out.println("==输出结束==");
            }

            @Override
            public void onError(Throwable error) {
                error.printStackTrace();
            }
        });
    } @GetMapping("/fluxstreamhello")
    public Flux<String> fluxStreamHello(HttpServletResponse response){
        response.setCharacterEncoding("UTF-8");
        Flux<String> flux = Flux.create(fluxSink->{
            streamingChatModel.chat("你是谁", new StreamingChatResponseHandler() {

                @Override
                public void onPartialResponse(String partialResponse) {
                    fluxSink.next(partialResponse);
                }

                @Override
                public void onCompleteResponse(ChatResponse completeResponse) {
                    fluxSink.complete();
                }

                @Override
                public void onError(Throwable error) {
                    fluxSink.error(error);
                }
            });
        });
        return flux;
    }
    @RequestMapping("/memory")
    public String memory(HttpServletResponse response) {
        List<ChatMessage> messages = new ArrayList<>();

        //第一轮对话
        messages.add(systemMessage("你是一个AI助手"));
        messages.add(userMessage("我叫Hollis，是一个程序员"));
        AiMessage answer = chatModel.chat(messages).aiMessage();
        System.out.println(answer);
        System.out.println("======");

        messages.add(answer);

        //第二轮对话
        messages.add(userMessage("Hollis是干什么的?"));
        AiMessage answer1 = chatModel.chat(messages).aiMessage();
        System.out.println(answer1);
        System.out.println("======");

        messages.add(answer1);

        //第三轮对话
        messages.add(userMessage("我是谁？"));
        AiMessage answer2 = chatModel.chat(messages).aiMessage();
        System.out.println(answer2);
        System.out.println("======");

        return answer2.text();
    }
}
