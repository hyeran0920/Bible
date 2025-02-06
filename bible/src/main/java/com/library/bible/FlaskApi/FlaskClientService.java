package com.library.bible.FlaskApi;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class FlaskClientService {
    
    private final String FLASK_API_URL = "http://127.0.0.1:5000/recommend"; // Flask의 Ngrok URL

    public String getRecommendation(int userId, int n) {
        RestTemplate restTemplate = new RestTemplate();
        
        String requestUrl = FLASK_API_URL + "?user_id=" + userId + "&n=" + n;
        
        // JSON 데이터 생성
        //Map<String, Integer> requestData = new HashMap<>();
        //requestData.put("input", inputValue);
        return restTemplate.getForObject(FLASK_API_URL, String.class, userId, 10);
    }
}