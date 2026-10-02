package com.kaziki.springai.controller;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.kaziki.springai.embedding.EmbeddingService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rag/retriever")
public class RagRetrieverController implements InitializingBean {
    @Autowired
    private EmbeddingService embeddingService;
    @Autowired
    private ChatModel chatModel;

    @Autowired
    private PgVectorStore vectorStore;
    @GetMapping("/query")
    private String getRetriever(String query){
        List<Document> documents=embeddingService.similaritySearch(query);
        StringBuffer stringBuffer=new StringBuffer();
        for (Document document: documents){
            System.out.println(document.getText()+"//");
            stringBuffer.append(document.getText());
        }
        return stringBuffer.toString();
    }
    @GetMapping("/retriever")
    private String retriever(String query){
        List<Document> documents=embeddingService.similaritySearch(query);
        String documentContent=documents.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n============文本分割线============\n\n"));
        String promptTemplate= """
                请基于下面提供的参考文档和内容,回答用户的问题.
                参考文档没有相关信息,请直接说明"没有找到相关信息",不要胡编乱造内容.
                参考文档:{documents}
                用户问题:{query}
                """;
        PromptTemplate prompt=new PromptTemplate(promptTemplate);

        Prompt realPrompt=prompt.create(Map.of("query", query, "documents", documents));
        return chatModel.call(realPrompt).getResult().getOutput().getText();
    }
    private ChatClient chatClient;

    @GetMapping("/retrieverAdvisor")
    private String retrieverAdvisor(String query){
        return chatClient.prompt(query).call().content();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        PromptTemplate promptTemplate=new PromptTemplate("""
                请基于下面提供的参考文档和内容,回答用户的问题.
                参考文档没有相关信息,请直接说明"没有找到相关信息",不要胡编乱造内容.
                参考文档:{question_answer_context}
                用户问题:{query}
                """);
        QuestionAnswerAdvisor questionAnswerAdvisor=QuestionAnswerAdvisor.builder(vectorStore)
                .searchRequest(SearchRequest.builder().topK(5).similarityThreshold(0.5).build())
                .promptTemplate(promptTemplate).build();
        this.chatClient=ChatClient.builder(chatModel)
                .defaultAdvisors(questionAnswerAdvisor)
                .defaultOptions(
                        DashScopeChatOptions.builder()
                                        .withTopP(0.7)
                                .build()
                ).build();
    }
}
