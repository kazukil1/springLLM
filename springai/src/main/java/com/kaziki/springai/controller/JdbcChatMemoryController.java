
package com.kaziki.springai.controller;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
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
@RequestMapping("/jdbcmemory")
public class JdbcChatMemoryController implements InitializingBean {

    @Autowired
    private ChatModel chatModel;

    private ChatClient chatClient;
    @Autowired
    private ChatMemory jdbcChatMemory;
    @Override
    public void afterPropertiesSet() throws Exception {
/*        chatClient=ChatClient.builder(chatModel)
                //实现logger的advisor
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .defaultSystem("你是一个贴心的小女友")
                //设置模型参数
                .defaultOptions(DashScopeChatOptions.builder().temperature(0.7).build())
                .build();*/

        this.chatClient = ChatClient.builder(chatModel)
                // 实现 Logger 的 Advisor
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(jdbcChatMemory).build())
                // 设置 ChatClient 中 ChatModel 的 Options 参数
                .defaultOptions(
                        DashScopeChatOptions.builder()
                                .withTopP(0.7)
                                .build()
                )
                .build();
    }
    @GetMapping("/callConversation")
    public Flux<String>  callConversation(String message, String chatId, HttpServletResponse response){
        response.setCharacterEncoding("UTF-8");
        return  chatClient.prompt()
                .user(message)
                .advisors(spec->spec.param(ChatMemory.CONVERSATION_ID,chatId)
                        .param("chat_memory_retrieve_size",100))
                .stream().content();
    }
}