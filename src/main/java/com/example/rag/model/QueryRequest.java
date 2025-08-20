package com.example.rag.model;


import jakarta.validation.constraints.NotBlank;


public class QueryRequest {
    @NotBlank
    private String question;
    private Integer topK; // optional override
    private Double minScore; // optional override


    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    public Integer getTopK() { return topK; }
    public void setTopK(Integer topK) { this.topK = topK; }
    public Double getMinScore() { return minScore; }
    public void setMinScore(Double minScore) { this.minScore = minScore; }
}