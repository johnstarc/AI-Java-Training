package org.example;

import java.util.Scanner;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;

public class ChatBotPreservingChatHistory {
    interface Assistant { String chat(String m); }
    public static void main(String[] args) {
        var assistant = AiServices.builder(Assistant.class)
              .chatLanguageModel(OllamaChatModel.builder().baseUrl("http://localhost:11434").modelName("gemma3:1b").build())
              .chatMemory(MessageWindowChatMemory.withMaxMessages(10)).build();
        var s = new Scanner(System.in);
        while (true) {
            System.out.print("User: ");
            String in = s.nextLine();
            if (in.equalsIgnoreCase("exit")) break;
            try { System.out.println("AI: " + assistant.chat(in)); } catch (Exception e) { break; }
        }
        System.exit(0);
    }
}