package com.kaziki.springai.controller;

import com.alibaba.cloud.ai.transformer.splitter.RecursiveCharacterTextSplitter;
import com.kaziki.springai.cleaner.DocumentCleaner;
import com.kaziki.springai.embedding.EmbeddingService;
import com.kaziki.springai.reader.DocumentReaderFactory;
import com.kaziki.springai.spiltter.OverlapParagraphTextSplitter;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/rag/embedding")
public class RagEmbeddingController {


    @Autowired
    private EmbeddingModel embeddingModel;
    @Autowired
    private DocumentReaderFactory documentReaderFactory;
    @Autowired
    private EmbeddingService embeddingService;
    @Autowired
    private PgVectorStore vectorStore;

    @GetMapping("/add")
    public String add() {
        System.out.println("API Key loaded: " + System.getenv("DASHSCOPE_API_KEY"));
        embeddingModel.embed("Hello, world!");
        return "Rag Embedding Test";
    }

    /**
     * 向量化文件并存储
     * @param filePath
     * @return
     */
    @GetMapping("/embed")
    public String embed(String  filePath){
        List<Document> documents = new ArrayList<>();
        try{
            documents=documentReaderFactory.read(new File(filePath));
        }catch (Exception e){
            e.printStackTrace();
        }
        documents=DocumentCleaner.cleanDocuments(documents);
        List<Document> chunks = new ArrayList<>();
        for(Document document: documents){
            OverlapParagraphTextSplitter splitter=new OverlapParagraphTextSplitter(1000,50);
            chunks.addAll(splitter.split(document));

        }
        embeddingService.embedAndStore(chunks);
        return "Embedding and storing completed.";
    }


}
