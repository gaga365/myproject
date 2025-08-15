package com.assistant.personalsystem.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${google.ai.api.key}")
    private String apiKey;

    private static final String GOOGLE_AI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent"; // Using gemini-pro as it's generally recommended for text generation

    @Value("${deepseek.api.key}")
    private String deepseekApiKey;
    private static final String DEEPSEEK_API_URL = "https://api.deepseek.com/v1/chat/completions";

    public String getAnswer(String question) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Construct the request body based on Google AI API format
            Map<String, Object> requestBody = new HashMap<>();
            Map<String, Object> part = new HashMap<>();
            part.put("text", question);
            Map<String, Object> content = new HashMap<>();
            content.put("parts", Collections.singletonList(part));
            requestBody.put("contents", Collections.singletonList(content));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            String apiUrlWithKey = GOOGLE_AI_API_URL + "?key=" + apiKey;

            log.info("Calling Google AI API with question: {}", question);

            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    apiUrlWithKey,
                    HttpMethod.POST,
                    entity,
                    JsonNode.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode root = response.getBody();
                // Assuming the structure is candidates[0].content.parts[0].text
                JsonNode textNode = root.at("/candidates/0/content/parts/0/text");
                if (textNode.isTextual()) {
                    log.info("Successfully received answer from Google AI.");
                    return textNode.asText();
                }
            }

            log.error("Failed to get valid response from Google AI API. Status code: {}, Body: {}", 
                    response.getStatusCode(), response.getBody());
            return "抱歉，未能获取到答案。";

        } catch (Exception e) {
            log.error("Error calling Google AI API", e);
            return "抱歉，在获取答案时发生错误。";
        }
    }

    public String translateWithDeepSeek(String text, String from, String to) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + deepseekApiKey);

            String prompt = String.format("Translate the following text from %s to %s: %s", from, to, text);
            Map<String, Object> body = new HashMap<>();
            body.put("model", "deepseek-chat");
            java.util.List<java.util.Map<String, String>> messages = new java.util.ArrayList<>();
            messages.add(java.util.Map.of(
                "role", "system",
                "content", "You are a translation assistant."
            ));
            messages.add(java.util.Map.of(
                "role", "user",
                "content", prompt
            ));
            body.put("messages", messages);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                DEEPSEEK_API_URL,
                HttpMethod.POST,
                entity,
                JsonNode.class
            );
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode root = response.getBody();
                JsonNode textNode = root.at("/choices/0/message/content");
                if (textNode.isTextual()) {
                    return textNode.asText();
                }
            }
            return "翻译失败";
        } catch (Exception e) {
            log.error("DeepSeek翻译异常", e);
            return "翻译出错";
        }
    }
} 