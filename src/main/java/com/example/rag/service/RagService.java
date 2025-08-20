package com.example.rag.service;


import com.example.rag.model.QueryRequest;
import com.example.rag.model.QueryResponse;
import com.example.rag.model.SourceChunk;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RagService {


    private final EmbeddingModel embeddingModel;
    private final ChatLanguageModel chatModel;
    private final EmbeddingStore<TextSegment> embeddingStore;
    private final DocumentSplitter splitter;
    private final int defaultTopK;
    private final double defaultMinScore;


    public RagService(EmbeddingModel embeddingModel,
                      ChatLanguageModel chatModel,
                      EmbeddingStore<TextSegment> embeddingStore,
                      DocumentSplitter splitter,
                      @Value("${rag.retrieval.top-k:5}") int defaultTopK,
                      @Value("${rag.retrieval.min-score:0.60}") double defaultMinScore) {
        this.embeddingModel = embeddingModel;
        this.chatModel = chatModel;
        this.embeddingStore = embeddingStore;
        this.splitter = splitter;
        this.defaultTopK = defaultTopK;
        this.defaultMinScore = defaultMinScore;
    }


    /** Ingest plain text under a logical sourceId. */
    public Mono<Void> ingestText(String sourceId, String text) {
        return Mono.fromRunnable(() -> {

            Metadata md = Metadata.from("sourceId", sourceId);
            Document doc = Document.from(text, md);

// Manually split + embed + store (equivalent to EmbeddingStoreIngestor)
            List<TextSegment> segments = splitter.split(doc);
            var embeddings = embeddingModel.embedAll(segments).content();
            embeddingStore.addAll(embeddings, segments);
        }).subscribeOn(Schedulers.boundedElastic()).then();
    }


    /** Query RAG: find relevant chunks, build a grounded prompt, ask LLM. */
    public Mono<QueryResponse> query(QueryRequest req) {
        final int k = req.getTopK() != null ? req.getTopK() : defaultTopK;
        final double minScore = req.getMinScore() != null ? req.getMinScore() : defaultMinScore;


        return Mono.fromCallable(() -> {
            var queryEmbedding = embeddingModel.embed(req.getQuestion()).content();
            EmbeddingSearchRequest search = EmbeddingSearchRequest.builder()
                    .queryEmbedding(queryEmbedding)
                    .maxResults(k)
                    .minScore(minScore)
                    .build();
            EmbeddingSearchResult<TextSegment> result = embeddingStore.search(search);


            List<EmbeddingMatch<TextSegment>> matches = result.matches();
            String context = matches.stream()
                    .map(m -> m.embedded().text())
                    .collect(Collectors.joining("\n\n---\n\n"));


            String prompt = """
You are a helpful assistant. Answer the user's question using ONLY the context below.
If the answer is not present, say: "I don't know based on the provided data."


Context:
%s


Question: %s
Provide a concise, accurate answer:
""".formatted(context, req.getQuestion());


            String answer = chatModel.generate(prompt);


            List<SourceChunk> sources = new ArrayList<>();
            for (EmbeddingMatch<TextSegment> m : matches) {
               String src = m.embedded().metadata() != null
                        ? m.embedded().metadata().getString("sourceId")
                        : null;
                String snippet = m.embedded().text();
                if (snippet.length() > 280) snippet = snippet.substring(0, 280) + "...";
                sources.add(new SourceChunk(src, snippet, m.score() != null ? m.score() : 0.0));
            }


            return new QueryResponse(answer, sources);
        }).subscribeOn(Schedulers.boundedElastic());
    }
}