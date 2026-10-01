package com.kaziki.springai.controller;

import com.alibaba.cloud.ai.transformer.splitter.RecursiveCharacterTextSplitter;
import com.kaziki.springai.embedding.EmbeddingService;
import com.kaziki.springai.reader.DocumentReaderFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
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

    @GetMapping("/add")
    public String add() {
        System.out.println("API Key loaded: " + System.getenv("DASHSCOPE_API_KEY"));
        embeddingModel.embed("Hello, world!");
        return "Rag Embedding Test";
    }

    @GetMapping("/embed")
    public String embed(String  filePath){
        List<Document> documents = new ArrayList<>();
        try{
            documents=documentReaderFactory.read(new File(filePath));
        }catch (Exception e){
            e.printStackTrace();
        }

        List<Document> chunks = new ArrayList<>();
        for(Document document: documents){
            RecursiveCharacterTextSplitter splitter=new RecursiveCharacterTextSplitter(300,new String[]{"\n\n","\n"});
            chunks.addAll(splitter.split(document));

        }
        embeddingService.embedAndStore(chunks);
        return "Embedding and storing completed.";
    }
}
