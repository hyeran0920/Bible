package com.library.bible.FlaskApi;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class FlaskClientService {
    
    private final String FLASK_API_URL = "https://8f50-112-221-184-61.ngrok-free.app"; // Flask의 Ngrok URL

    public String getRecommendation(int inputValue) {
        RestTemplate restTemplate = new RestTemplate();
        
        // JSON 데이터 생성
        Map<String, Integer> requestData = new HashMap<>();
        requestData.put("input", inputValue);
        
        // Flask API 요청
        ResponseEntity<String> response = restTemplate.postForEntity(FLASK_API_URL, requestData, String.class);
        
        return response.getBody();
    }
}