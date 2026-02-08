package org.example;

import java.util.Scanner;

import dev.langchain4j.model.openai.OpenAiChatModel;

public class CerebrasChatBot {
    public static void main(String[] args) {
        // Get Cerebras API Key from environment variable
        String apiKey = System.getenv("CEREBRAS_API_KEY");
        apiKey = "csk-j2nfehme5dnrk4j6rxk8fn52v3pjn9w5kh65pdmt3j8dettp";

        if (apiKey == null || apiKey.isEmpty()) {
            System.out.println("Error: CEREBRAS_API_KEY environment variable is not set.");
            return;
        }

        // Cerebras is OpenAI-compatible, so we use OpenAiChatModel with their base URL
        OpenAiChatModel model = OpenAiChatModel.builder()
              .baseUrl("https://api.cerebras.ai/v1")
              .apiKey(apiKey)
              .modelName("gpt-oss-120b") // Cerebras cloud model
              .build();

        Scanner scanner = new Scanner(System.in);
        System.out.println("Cerebras Chatbot started! Type 'exit' to quit.");

        while (true) {
            System.out.print("You: ");
            String input = scanner.nextLine();
            if ("exit".equalsIgnoreCase(input)) break;

            String response = model.chat(input);
            System.out.println("AI: " + response);
        }
        System.exit(0);
    }
}