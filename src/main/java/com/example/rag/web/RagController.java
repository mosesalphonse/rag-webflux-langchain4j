package com.example.rag.web;


import com.example.rag.model.IngestTextRequest;
import com.example.rag.model.QueryRequest;
import com.example.rag.model.QueryResponse;
import com.example.rag.service.RagService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class RagController {


    private final RagService ragService;


    public RagController(RagService ragService) { this.ragService = ragService; }


    @PostMapping(path = "/ingest/text", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Void> ingestText(@RequestBody @Valid IngestTextRequest req) {
        return ragService.ingestText(req.getSourceId(), req.getText());
    }


    @PostMapping(path = "/query", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<QueryResponse> query(@RequestBody @Valid QueryRequest req) {
        return ragService.query(req);
    }
}