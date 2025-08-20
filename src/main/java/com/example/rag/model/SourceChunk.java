package com.example.rag.model;


public class SourceChunk {
    private String sourceId;
    private String snippet;
    private double score;


    public SourceChunk() {}
    public SourceChunk(String sourceId, String snippet, double score) {
        this.sourceId = sourceId;
        this.snippet = snippet;
        this.score = score;
    }
    public String getSourceId() { return sourceId; }
    public void setSourceId(String sourceId) { this.sourceId = sourceId; }
    public String getSnippet() { return snippet; }
    public void setSnippet(String snippet) { this.snippet = snippet; }
    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }
}