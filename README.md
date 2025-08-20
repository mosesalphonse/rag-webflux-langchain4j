# RAG WebFlux + LangChain4j


### Prereqs
* Java 22+
* Maven 3.9+
* An OpenAI API key exported as `OPENAI_API_KEY` (or edit `application.yml`).


### Run
bash

```
mvn spring-boot:run

```

### Try it

```
curl -X POST http://localhost:8080/api/ingest/text \
-H 'Content-Type: application/json' \
-d '{"sourceId":"docs-1","text":"Spring WebFlux is a reactive, non-blocking web framework."}'

```

```

curl -X POST http://localhost:8080/api/query \
-H 'Content-Type: application/json' \
-d '{"question":"What is Spring WebFlux?"}' | jq

```

### Tests

# Runs unit + Cucumber BDD. They are enabled only if OPENAI_API_KEY is present.
```
mvn -Dtest=!* -Dit.test=* test

```

## ⚙️ How the Flow Works (End-to-End)

### 📝 Ingest
- Request arrives → `RagController.ingestText()` → `RagService.ingestText()`.
- Text becomes a `Document` with `Metadata.from("sourceId", sourceId)`, split into `TextSegment`s.
- Embeddings are computed and stored in the **in-memory `EmbeddingStore`**.

### 🔍 Retrieve
- Query arrives → `RagService.query()` embeds the question.
- Vector search returns top-K `TextSegment`s above `minScore`.
- Segments are concatenated into **Context**; a grounded prompt instructs the LLM to answer only from that context.

### 🤖 Generate
- `chatModel.generate(prompt)` produces the answer.
- Response includes:
  - **Answer**  
  - **Sources** (snippet + `sourceId` via `metadata.getString("sourceId")`).

---

## ✅ What the POC Proves
- **Pure-Java RAG** → Full pipeline (ingest → retrieve → generate) in Spring Boot using LangChain4j — no Python required.  
- **Grounded answers** → LLM relies only on retrieved chunks; responses return **citations** for traceability.  
- **Composable & swappable**:  
  - Replace in-memory store with **pgvector / Elastic / Milvus / Weaviate / MongoDB Atlas / Neo4j**.  
  - Swap models (Azure OpenAI, Ollama, Cohere, etc.) with minimal changes.  
- **Reactive & testable API** → WebFlux endpoints are non-blocking; JUnit + Cucumber validate the happy path.

---

## ⚠️ Important Notes
- Current store is **volatile** (lost on restart). For prod, use a persistent vector DB.  
- Use the **non-deprecated Metadata API**:  
  - Build → `Metadata.from("sourceId", sourceId)` (or builder).  
  - Read → `metadata.getString("sourceId")`.

---

## 🚀 Next Steps (Prod-Ready)
- Add a persistent **EmbeddingStore** (e.g., pgvector) and schema migrations.  
- Enrich metadata with **chunk provenance** (filename, page, URL).  
- Add **observability**: timings, hit ratio, response quality.  
- Introduce **guardrails**: max tokens, content filters, caching.  
- Support **batch ingestion** (files/URLs) and background jobs.  
