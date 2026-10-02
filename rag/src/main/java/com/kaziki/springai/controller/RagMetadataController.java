package com.kaziki.springai.controller;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.kaziki.springai.embedding.EmbeddingService;
import com.kaziki.springai.reader.DocumentReaderFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/rag/metadata")
public class RagMetadataController implements InitializingBean {
    @Autowired
    private EmbeddingService embeddingService;
    @Autowired
    private DocumentReaderFactory documentReaderFactory;
    @Autowired
    private ChatModel chatModel;
    @Autowired
    private PgVectorStore vectorStore;
    @GetMapping("/embed")
    public String embedding(String filePath,String fileName){
        List<Document> documents;
        try{
            documents=documentReaderFactory.read(new File(filePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (Document document : documents) {
            document.getMetadata().put("fileName", fileName);
        }
        embeddingService.embedAndStore(documents);
        return "Embedding completed";
    }

    @GetMapping("/retrieveMetadata")
    public String retrieveMetadata(String query,String fileName) {
        SearchRequest request = SearchRequest.builder().query(query).filterExpression("fileName == '" + fileName + "'").build();
        return embeddingService.similaritySearch(request).toString();
    }

    private ChatClient chatClient;
    @GetMapping("/retrieveAdvisorsWithMetadata")
    private String retrieveAdvisors(String query, String fileName) {
        return chatClient.prompt(query)
                .advisors(advisorSpec -> advisorSpec.param("qa_filter_expression", "fileName == '" + fileName + "'"))
                .call().content();
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
        this.chatClient= ChatClient.builder(chatModel)
                .defaultAdvisors(questionAnswerAdvisor)
                .defaultOptions(
                        DashScopeChatOptions.builder()
                                .withTopP(0.7)
                                .build()
                ).build();
    }
}
