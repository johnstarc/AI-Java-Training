package org.example;

import dev.langchain4j.model.openai.OpenAiChatModel;

public class CerebrasExample {
    public static void main(String[] args) {
        // Use your Cerebras API Key (set it as an environment variable)
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
              .modelName("gpt-oss-120b") // Or use "gpt-oss-120b" as per your preference
              .build();

        String response = model.chat("Why is fast inference important for Java developers?");
        System.out.println("Cerebras (gpt-oss-120b) Response: " + response);
        // Force exit to prevent OkHttp threads from lingering
        System.exit(0);
    }
}