package com.assistant.personalsystem.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api")
public class SearchController {
    private static final Logger logger = LoggerFactory.getLogger(SearchController.class);

    @Value("${deepseek.api.key}")
    private String apiKey;

    @Value("${deepseek.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody Map<String, String> request) {
        try {
            String query = request.get("query");
            logger.info("收到搜索请求: {}", query);
            
            if (query == null || query.trim().isEmpty()) {
                logger.warn("搜索内容为空");
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "搜索内容不能为空"));
            }
            
            // 准备请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);
            logger.debug("API密钥: {}", apiKey);

            // 准备请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "deepseek-chat");
            
            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "system", "content", "你是一个智能助手，请用简洁专业的方式回答问题。"));
            messages.add(Map.of("role", "user", "content", query));
            requestBody.put("messages", messages);
            
            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", 1000);

            logger.debug("发送到DeepSeek API的请求: {}", requestBody);

            // 发送请求到DeepSeek API
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                entity,
                Map.class
            );

            logger.debug("DeepSeek API响应: {}", response.getBody());

            // 处理响应
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> choice = choices.get(0);
                    Map<String, Object> message = (Map<String, Object>) choice.get("message");
                    String content = (String) message.get("content");
                    logger.info("搜索成功，返回结果: {}", content);
                    return ResponseEntity.ok(Map.of("response", List.of(Map.of(
                        "message", Map.of("content", content)
                    ))));
                }
            }

            logger.error("无法获取有效的响应: {}", responseBody);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "无法获取有效的响应"));

        } catch (Exception e) {
            logger.error("搜索请求处理失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "搜索请求处理失败: " + e.getMessage()));
        }
    }

    @PostMapping("/search/export")
    public void exportSearchResult(@RequestParam("content") String content, HttpServletResponse response) throws java.io.IOException {
        String fileName = "search-" + System.currentTimeMillis() + ".txt";
        response.setContentType("text/plain");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        response.getWriter().write(content);
        response.getWriter().flush();
    }

    @GetMapping("/search/fragment")
    public ModelAndView searchFragment() {
        return new ModelAndView("search-fragment");
    }
} 