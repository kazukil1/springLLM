package com.kaziki.springai.embedding;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmbeddingService {
    @Autowired
    private EmbeddingModel embeddingModel;
    public void embed() {
        Object embed = embeddingModel.embed("Hello, world!");
    }
}
