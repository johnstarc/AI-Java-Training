package org.example;

import dev.langchain4j.model.ollama.OllamaChatModel;

public class ConnectingWithOllama {
    public static void main(String[] args) {
        // Build the connection to local Ollama (default port 11434)
        OllamaChatModel model = OllamaChatModel.builder()
              .baseUrl("http://localhost:11434")
              .modelName("gemma3:1b") // Ensure you have pulled this model via 'ollama pull llama3'
              .build();

        // Generate a response and print it
        String response = model.chat("Explain Java in one sentence.");
        System.out.println("AI Response: " + response);

        // Force exit to prevent OkHttp threads from lingering
        System.exit(0);
    }
}