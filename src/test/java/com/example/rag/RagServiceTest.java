package com.example.rag;


import com.example.rag.model.QueryRequest;
import com.example.rag.model.QueryResponse;
import com.example.rag.service.RagService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;


import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".+")
class RagServiceTest {


    @Autowired
    RagService ragService;


    @Test
    void ingestAndQuery_happyPath() {
        ragService.ingestText("sample", "Spring WebFlux is a reactive, non-blocking web stack.")
                .block();
        QueryRequest req = new QueryRequest();
        req.setQuestion("What is Spring WebFlux?");
        QueryResponse resp = ragService.query(req).block();
        assertNotNull(resp);
        assertNotNull(resp.getAnswer());
        assertFalse(resp.getSources().isEmpty());
    }
}