package org.example.Day3;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;
import java.util.TimeZone;

import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

public class PostgressQueryAgent {
    // Update these connection details for your database
    // H2 in-memory DB URL
    static String DB_URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
    static String DB_USER = "sa";
    static String DB_PASS = "";

    public static void main(String[] args) throws Exception {
        // Fix timezone issue
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        // Setup Ollama Chat model
//        var model = OllamaChatModel.builder()
//              .baseUrl("http://localhost:11434")
//              .modelName("gemma3:1b")
//              .build();

        // Cerebras is OpenAI-compatible, so we use OpenAiChatModel with their base URL
        String apiKey = "csk-j2nfehme5dnrk4j6rxk8fn52v3pjn9w5kh65pdmt3j8dettp";
        OpenAiChatModel model = OpenAiChatModel.builder()
              .baseUrl("https://api.cerebras.ai/v1")
              .apiKey(apiKey)
              .modelName("gpt-oss-120b") // Cerebras cloud model
              .build();


        // Initialize H2 DB with sample tables
        initDatabase();

        // Get schema for context
        String schema = getSchema();
        String systemPrompt = """
                You are a SQL assistant. Given the schema below, generate only SQL queries.
                Return ONLY the SQL query, no explanations.
                Schema:
                """ + schema;

        Scanner scanner = new Scanner(System.in);
        System.out.println("H2 Query Agent. Type 'exit' to quit.\n");

        while (true) {
            System.out.print("You: ");
            String question = scanner.nextLine();
            if (question.equalsIgnoreCase("exit")) break;

            // Generate SQL from natural language
            String sql = model.chat(systemPrompt + "\nQuestion: " + question);
            System.out.println("Generated SQL: " + sql);

            // Execute and show results
            try {
                String result = executeQuery(sql.trim());
                System.out.println("Result:\n" + result + "\n");
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage() + "\n");
            }
        }

        scanner.close();
    }

    static void initDatabase() throws Exception {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
              Statement stmt = conn.createStatement()) {

            // Create sample tables
            stmt.execute("CREATE TABLE cars (id INT PRIMARY KEY, brand VARCHAR(50), model VARCHAR(50), manufacture_year INT)");
            stmt.execute("INSERT INTO cars VALUES (1, 'Tesla', 'Model S', 2022)");
            stmt.execute("INSERT INTO cars VALUES (2, 'Toyota', 'Corolla', 2020)");
            stmt.execute("INSERT INTO cars VALUES (3, 'Ford', 'Mustang', 2021)");

            stmt.execute("CREATE TABLE sales (id INT PRIMARY KEY, car_id INT, quantity INT, price DECIMAL)");
            stmt.execute("INSERT INTO sales VALUES (1, 1, 5, 79999.99)");
            stmt.execute("INSERT INTO sales VALUES (2, 2, 12, 19999.99)");
            stmt.execute("INSERT INTO sales VALUES (3, 3, 7, 55999.99)");
        }
    }

    static String getSchema() throws Exception {
        StringBuilder sb = new StringBuilder();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            var meta = conn.getMetaData();
            var tables = meta.getTables(null, null, "%", new String[]{"TABLE"});
            while (tables.next()) {
                String table = tables.getString("TABLE_NAME");
                sb.append("Table: ").append(table).append(" (");
                var cols = meta.getColumns(null, null, table, "%");
                while (cols.next()) {
                    sb.append(cols.getString("COLUMN_NAME")).append(" ");
                    sb.append(cols.getString("TYPE_NAME")).append(", ");
                }
                sb.append(")\n");
            }
        }
        return sb.toString();
    }

    static String executeQuery(String sql) throws Exception {
        StringBuilder sb = new StringBuilder();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
              Statement stmt = conn.createStatement();
              ResultSet rs = stmt.executeQuery(sql)) {

            var meta = rs.getMetaData();
            int cols = meta.getColumnCount();

            while (rs.next()) {
                for (int i = 1; i <= cols; i++) {
                    sb.append(meta.getColumnName(i)).append(": ");
                    sb.append(rs.getString(i)).append(" | ");
                }
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}