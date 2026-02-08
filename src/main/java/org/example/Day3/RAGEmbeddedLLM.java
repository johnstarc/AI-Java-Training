package org.example.Day3;

import java.nio.file.Path;
import java.util.Scanner;

import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

public class RAGEmbeddedLLM {
    public static void main(String[] args) {
        // Setup embedding model
        var embedModel = OllamaEmbeddingModel.builder()
              .baseUrl("http://localhost:11434")
              .modelName("nomic-embed-text:latest").build();

        // Setup chat model
        var chatModel = OllamaChatModel.builder()
              .baseUrl("http://localhost:11434")
              .modelName("gemma3:1b").build();

        // Load and parse PDF
        var parser = new ApachePdfBoxDocumentParser();
        var doc = FileSystemDocumentLoader.loadDocument(
              Path.of("src/main/resources/About_HERE_AND_NOW_AI.pdf"), parser);

        // Split and embed into vector store
        var store = new InMemoryEmbeddingStore<TextSegment>();
        var splitter = DocumentSplitters.recursive(300, 50);
        for (var chunk : splitter.split(doc)) {
            var segment = TextSegment.from(chunk.text());
            store.add(embedModel.embed(segment).content(), segment);
        }

        // Setup retriever
        var retriever = EmbeddingStoreContentRetriever.builder()
              .embeddingStore(store)
              .embeddingModel(embedModel)
              .maxResults(3).build();

        Scanner scanner = new Scanner(System.in);
        System.out.println("PDF RAG (Vector Embeddings). Type 'exit' to quit.\n");
        while (true) {
            System.out.print("You: ");
            String question = scanner.nextLine();
            if (question.equalsIgnoreCase("exit")) break;

            // Retrieve relevant context using embeddings
            var results = retriever.retrieve(
                  dev.langchain4j.rag.query.Query.from(question));
            String context = results.stream()
                  .map(c -> c.textSegment().text())
                  .reduce("", (a, b) -> a + "\n---\n" + b);

            String prompt = "Context:\n" + context + "\n\nQuestion: " + question;
            System.out.println("Bot: " + chatModel.chat(prompt) + "\n");
        }
        scanner.close();
    }
}