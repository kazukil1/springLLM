package com.kaziki.springai.function;

import com.kaziki.springai.model.ChatStatus;
import com.kaziki.springai.model.OrderChat;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.UUID;

@RestController
@RequestMapping("/pdd/refund")
@Slf4j
@RequiredArgsConstructor
public class PddRefundController {
    @Autowired
    private OpenAiChatModel chatModel;

    private ChatClient chatClient;
    @Autowired
    private OrderTools orderTools;
    @Value("classpath:prompts/pdd_refund_system_prompt.pt")
    private Resource systemPrompt;

    @GetMapping("/newChat")
    public OrderChat newChat(String userId, String orderId, HttpServletResponse response){
        response.setCharacterEncoding("UTF-8");
        //模拟数据库创建一个chat的记录,获取唯一id
        String chatId= UUID.randomUUID().toString();
        return chatClient.prompt().user(String.format("我想咨询订单相关的首行问题,我的用户id:%s,我的订单号:%s,本次对话id:%s,当前状态是%s", userId, orderId, chatId, ChatStatus.CHAT_START))
                .advisors(spec->spec.param(ChatMemory.CONVERSATION_ID,chatId).param("chat_memory_retrieve_size",100))
                .call().entity(OrderChat.class);

    }
    @GetMapping("/chat")
    public String chat(){
        return null;
    }
    // PddRefundController




    @GetMapping("/ask")
    public Flux<String> ask(String question, String chatId, HttpServletResponse httpServletResponse) {
        httpServletResponse.setCharacterEncoding("UTF-8");

        return chatClient
                .prompt()
                .user(question).tools(orderTools)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId)
                        .param("chat_memory_retrieve_size", 100))
                .stream().content();
    }
    @PostConstruct
    public void init(){
        ChatMemory chatMemory=MessageWindowChatMemory.builder().maxMessages(10).build();
        chatClient=ChatClient.builder(chatModel)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .defaultSystem(systemPrompt)
                .build();
    }
}
