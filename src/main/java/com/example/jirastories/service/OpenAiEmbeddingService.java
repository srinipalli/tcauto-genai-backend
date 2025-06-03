package com.example.jirastories.service;

import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OpenAiEmbeddingService {

    private final EmbeddingModel embeddingModel;

    @Autowired
    public OpenAiEmbeddingService(OpenAiEmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    public float[] getEmbedding(String text) {
        List<float[]> embeddings = embeddingModel.embed(List.of(text));
        return embeddings.get(0);
    }
}
