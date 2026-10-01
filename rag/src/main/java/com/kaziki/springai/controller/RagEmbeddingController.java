package com.kaziki.springai.controller;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rag/embedding")
public class RagEmbeddingController {


    @Autowired
    private EmbeddingModel embeddingModel;

    @GetMapping("/add")
    public String add() {
        System.out.println("API Key loaded: " + System.getenv("DASHSCOPE_API_KEY"));
        embeddingModel.embed("Hello, world!");
        return "Rag Embedding Test";
    }
}
