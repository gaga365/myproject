package com.assistant.personalsystem.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${weather.api.key}")
    private String apiKey;
    @Value("${weather.api.url}")
    private String weatherApiUrl;

    private static final Map<String, String> CITY_ADCODE_MAP = new HashMap<>();
    static {
        CITY_ADCODE_MAP.put("北京", "110000");
        CITY_ADCODE_MAP.put("上海", "310000");
        CITY_ADCODE_MAP.put("广州", "440100");
        CITY_ADCODE_MAP.put("深圳", "440300");
        CITY_ADCODE_MAP.put("天津", "120100");
        CITY_ADCODE_MAP.put("重庆", "500100");
        CITY_ADCODE_MAP.put("杭州", "330100");
        CITY_ADCODE_MAP.put("成都", "510100");
        CITY_ADCODE_MAP.put("武汉", "420100");
        CITY_ADCODE_MAP.put("南京", "320100");
        CITY_ADCODE_MAP.put("西安", "610100");
        CITY_ADCODE_MAP.put("苏州", "320500");
        CITY_ADCODE_MAP.put("青岛", "370200");
        CITY_ADCODE_MAP.put("郑州", "410100");
        CITY_ADCODE_MAP.put("长沙", "430100");
        CITY_ADCODE_MAP.put("沈阳", "210100");
        CITY_ADCODE_MAP.put("合肥", "340100");
        CITY_ADCODE_MAP.put("福州", "350100");
        CITY_ADCODE_MAP.put("厦门", "350200");
        CITY_ADCODE_MAP.put("济南", "370100");
        CITY_ADCODE_MAP.put("大连", "210200");
        CITY_ADCODE_MAP.put("宁波", "330200");
        CITY_ADCODE_MAP.put("东莞", "441900");
        CITY_ADCODE_MAP.put("佛山", "440600");
        // ...可继续补充常用城市
    }

    private String getAdcodeByCity(String city) {
        // 如果输入本身就是adcode，直接返回
        if (city.matches("\\d{6}")) {
            return city;
        }
        // 优先用本地Map查常用城市
        if (CITY_ADCODE_MAP.containsKey(city)) {
            return CITY_ADCODE_MAP.get(city);
        }
        try {
            String query = city;
            if (!city.endsWith("市")) {
                query = city + "市";
            }
            String encodedCity = URLEncoder.encode(query, StandardCharsets.UTF_8.toString());
            String url = "https://restapi.amap.com/v3/config/district?keywords=" + encodedCity + "&key=" + apiKey + "&subdistrict=2";
            log.info("请求高德行政区划API: {}", url.replace(apiKey, "******"));
            String response = restTemplate.getForObject(url, String.class);
            log.debug("高德行政区划API响应: {}", response);
            JsonNode root = objectMapper.readTree(response);
            if (!"1".equals(root.get("status").asText())) {
                String errorMsg = root.has("info") ? root.get("info").asText() : "未知错误";
                log.error("高德行政区划API返回错误: {}", errorMsg);
                throw new RuntimeException("获取adcode失败: " + errorMsg);
            }
            JsonNode districts = root.get("districts");
            if (districts == null || !districts.isArray() || districts.size() == 0) {
                throw new RuntimeException("未查询到城市adcode");
            }
            JsonNode main = districts.get(0);
            // 直辖市特殊处理
            if (main.has("citycode") && main.get("citycode").asText().isEmpty() && main.has("districts")) {
                JsonNode subDistricts = main.get("districts");
                if (subDistricts.isArray() && subDistricts.size() > 0) {
                    JsonNode sub = subDistricts.get(0);
                    if (sub.has("adcode")) {
                        return sub.get("adcode").asText();
                    }
                }
            }
            // 普通城市
            if (main.has("adcode")) {
                return main.get("adcode").asText();
            }
            throw new RuntimeException("未能获取有效adcode");
        } catch (Exception e) {
            log.error("获取adcode失败: city={}, error={}", city, e.getMessage(), e);
            throw new RuntimeException("获取adcode失败: " + e.getMessage());
        }
    }

    // 递归查找地级市/区县adcode
    private String findCityAdcode(JsonNode node) {
        if (node.has("adcode") && node.get("adcode").asText().length() == 6) {
            String adcode = node.get("adcode").asText();
            // 过滤省级adcode（以0000结尾），优先返回地级市/区县
            if (!adcode.endsWith("0000")) {
                return adcode;
            }
        }
        if (node.has("districts") && node.get("districts").isArray()) {
            for (JsonNode sub : node.get("districts")) {
                String adcode = findCityAdcode(sub);
                if (adcode != null) return adcode;
            }
        }
        return null;
    }

    public Map<String, String> getWeather(String city) {
        try {
            String adcode = getAdcodeByCity(city);
            String url = weatherApiUrl + "?city=" + adcode + "&key=" + apiKey + "&extensions=base&output=JSON";
            log.info("请求高德天气API: {}", url.replace(apiKey, "******"));
            String response = restTemplate.getForObject(url, String.class);
            log.debug("高德天气API响应: {}", response);
            JsonNode root = objectMapper.readTree(response);
            if (!"1".equals(root.get("status").asText())) {
                String errorMsg = root.has("info") ? root.get("info").asText() : "未知错误";
                log.error("高德天气API返回错误: {}", errorMsg);
                throw new RuntimeException("获取天气信息失败: " + errorMsg);
            }
            JsonNode lives = root.get("lives");
            if (lives == null || !lives.isArray() || lives.size() == 0) {
                throw new RuntimeException("未查询到天气信息");
            }
            JsonNode weather = lives.get(0);
            Map<String, String> weatherInfo = new HashMap<>();
            weatherInfo.put("city", weather.has("city") ? weather.get("city").asText() : "--");
            weatherInfo.put("weather", weather.has("weather") ? weather.get("weather").asText() : "--");
            weatherInfo.put("temperature", weather.has("temperature") ? weather.get("temperature").asText() : "--");
            weatherInfo.put("winddirection", weather.has("winddirection") ? weather.get("winddirection").asText() : "--");
            weatherInfo.put("windpower", weather.has("windpower") ? weather.get("windpower").asText() : "--");
            weatherInfo.put("humidity", weather.has("humidity") ? weather.get("humidity").asText() : "--");
            weatherInfo.put("reporttime", weather.has("reporttime") ? weather.get("reporttime").asText() : "--");
            log.info("成功获取城市[{}]的天气信息: {}，{}°C", 
                weatherInfo.get("city"), 
                weatherInfo.get("weather"), 
                weatherInfo.get("temperature"));
            return weatherInfo;
        } catch (Exception e) {
            log.error("获取天气信息失败: city={}, error={}", city, e.getMessage(), e);
            throw new RuntimeException("获取天气信息失败: " + e.getMessage());
        }
    }

    // 工具方法：打印常用城市的adcode
    public void printCommonAdcodes() {
        String[] cities = {"北京市", "上海市", "广州市", "深圳市", "杭州市", "成都市", "重庆市", "武汉市", "南京市", "西安市"};
        for (String city : cities) {
            try {
                String adcode = getAdcodeByCity(city);
                System.out.println(city + " 的adcode是：" + adcode);
            } catch (Exception e) {
                System.out.println(city + " 获取adcode失败: " + e.getMessage());
            }
        }
    }
}