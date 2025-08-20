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
