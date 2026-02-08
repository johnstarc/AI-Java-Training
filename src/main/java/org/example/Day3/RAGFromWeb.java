package org.example.Day3;

import java.util.Scanner;

import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.web.search.tavily.TavilyWebSearchEngine;

public class RAGFromWeb {
    public static void main(String[] args) {


        String apiKey = "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";

        // Get Tavily API key from environment
        // String apiKey = System.getenv("TAVILY_API_KEY");
        if (apiKey == null) {
            System.out.println("Set TAVILY_API_KEY environment variable in .env file");
            return;
        }

        // Setup search engine
        var searchEngine = TavilyWebSearchEngine.builder()
              .apiKey(apiKey)
              .searchDepth("advanced") // Use advanced for more comprehensive results
              .build();

        // Setup LLM
        var model = OllamaChatModel.builder()
              .baseUrl("http://localhost:11434")
              .modelName("gemma3:1b")
              .temperature(0.0) // Use temperature 0 for more factual answers
              .build();

        Scanner scanner = new Scanner(System.in);
        System.out.println("Search Agent (Tavily). Type 'exit' to quit.\n");

        while (true) {
            System.out.print("You: ");
            String question = scanner.nextLine();
            if (question.equalsIgnoreCase("exit")) break;

            // Search the web
            System.out.println("... searching web with Tavily ...");
            var response = searchEngine.search(question);
            var results = response.results();

            System.out.println("... found " + results.size() + " results ...");

            StringBuilder context = new StringBuilder();
            for (var result : results) {
                System.out.println("- Title: " + result.title());
                context.append("Title: ").append(result.title()).append("\n");
                context.append("Snippet: ").append(result.content()).append("\n");
                context.append("URL: ").append(result.url()).append("\n---\n");
            }

            if (results.isEmpty()) {
                System.out.println("Bot: I couldn't find any information on the web for that question.\n");
                continue;
            }

            String prompt = String.format("""
                You are a search assistant. Below are search results for the question: "%s"
                The current date is %s.
                
                Search Results:
                %s
                
                Instructions:
                1. Answer the question based ONLY on the search results provided.
                2. If the search results mention the current president or the 2024 election results, use that information.
                3. If the results are conflicting or old, point that out.
                4. cite the URL of the source you used.
                
                Answer:""", question, java.time.LocalDate.now(), context.toString());
            String answer = model.chat(prompt);
            System.out.println("Bot: " + answer + "\n");
        }
        scanner.close();
    }
}