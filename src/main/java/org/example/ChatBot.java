package org.example;

import java.util.Scanner;

import dev.langchain4j.model.ollama.OllamaChatModel;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class ChatBot {
    public static void main(String[] args) {
        OllamaChatModel model = OllamaChatModel.builder()
              .baseUrl("http://localhost:11434")
              .modelName("gemma3:1b") // Ensure you have pulled this model via 'ollama pull llama3'
              .build();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Chatbot started! Type 'exit' to quit.");

        while (true) {
            System.out.print("You: ");
            String input = scanner.nextLine();
            if ("exit".equalsIgnoreCase(input)) break;

            String response = model.chat(input);
            System.out.println("AI: " + response);
        }
        System.exit(0);

//        // Generate a response and print it
//        String response = model.chat("Explain Java in one sentence.");
//        System.out.println("AI Response: " + response);
//
//        // Force exit to prevent OkHttp threads from lingering
//        System.exit(0);
        }
    }