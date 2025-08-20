package com.example.rag.model;


import jakarta.validation.constraints.NotBlank;


public class IngestTextRequest {
    @NotBlank
    private String sourceId;
    @NotBlank
    private String text;


    public String getSourceId() { return sourceId; }
    public void setSourceId(String sourceId) { this.sourceId = sourceId; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
}