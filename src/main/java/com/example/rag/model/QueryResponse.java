package com.example.rag.model;


import java.util.List;


public class QueryResponse {
    private String answer;
    private List<SourceChunk> sources;


    public QueryResponse() {}
    public QueryResponse(String answer, List<SourceChunk> sources) {
        this.answer = answer;
        this.sources = sources;
    }
    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
    public List<SourceChunk> getSources() { return sources; }
    public void setSources(List<SourceChunk> sources) { this.sources = sources; }
}
