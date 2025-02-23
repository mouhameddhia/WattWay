package tn.esprit.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AIResponseService {
    private final String apiKey;
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent";

    public AIResponseService(String apiKey) {
        this.apiKey = apiKey;
    }

    public String generateResponse(String prompt) throws Exception {
        String urlString = API_URL + "?key=" + apiKey;
        HttpURLConnection conn = null;
        try {
            // Create connection
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            //Add instruction to the original prompt
            String modifiedPrompt = "Provide a short and informative response: " + prompt;

            // Add response format requirements in system message
            String structuredPrompt = String.format(
                    "You are a helpful assistant . " +
                            "Respond to the following query in 2-3 concise sentences. " +
                            "Be informative but brief and short . " +
                            " you can schedule an appointment with our mechanics to fix uur problem  "+
                            "Query: %s",
                    modifiedPrompt
            );
            // Build request body with structured prompt
            JSONObject requestBody = new JSONObject()
                    .put("contents", new JSONArray()
                            .put(new JSONObject()
                                    .put("parts", new JSONArray()
                                            .put(new JSONObject()
                                                    .put("text", structuredPrompt)
                                            )
                                    )
                            )
                    )
                    // Add generation config for length control
                    .put("generationConfig", new JSONObject()
                            .put("maxOutputTokens", 200)  // Limit response length
                            .put("temperature", 0.5)     // Control randomness (0-1)
                    );

            System.out.println("Request body: " + requestBody.toString());

            // Write request
            try (OutputStream os = conn.getOutputStream()) {
                os.write(requestBody.toString().getBytes(StandardCharsets.UTF_8));
            }

            // Handle response
            int responseCode = conn.getResponseCode();
            String responseBody = readResponseBody(conn, responseCode);
            System.out.println("Response code: " + responseCode);
            System.out.println("Raw response: " + responseBody);

            // Parse response
            JSONObject jsonResponse = new JSONObject(responseBody);

            // Check for errors first
            if (responseCode >= 400 || jsonResponse.has("error")) {
                String errorMessage = jsonResponse.optString("message", "Unknown API error");
                throw new Exception("API Error: " + errorMessage);
            }

            // Safely extract content
            JSONArray candidates = jsonResponse.optJSONArray("candidates");
            if (candidates == null || candidates.isEmpty()) {
                return "No response generated: Empty candidates array";
            }

            JSONObject firstCandidate = candidates.getJSONObject(0);
            JSONObject content = firstCandidate.optJSONObject("content");
            if (content == null) {
                return "No response generated: Missing content in candidate";
            }

            JSONArray parts = content.optJSONArray("parts");
            if (parts == null || parts.isEmpty()) {
                return "No response generated: Empty parts array";
            }

            String extractedText = parts.getJSONObject(0).optString("text", "");
            if (extractedText.isEmpty()) {
                return "No response generated: Empty text in part";
            }

            System.out.println("Extracted text: " + extractedText);
            return extractedText;

        } catch (JSONException e) {
            System.err.println("JSON parsing error: " + e.getMessage());
            return "Error parsing API response";
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private String readResponseBody(HttpURLConnection conn, int responseCode) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                responseCode >= 400 ? conn.getErrorStream() : conn.getInputStream(),
                StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining());
        }
    }
}