package com.library.bible.FlaskApi;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class FlaskClientService {
    
    private String FLASK_API_URL = "http://127.0.0.1:5000/recommend"; // Flask의 Ngrok URL
    private final RestTemplate restTemplate = new RestTemplate();

    private void FlaskClientService() {
        System.out.println("Flask API URL: " + this.FLASK_API_URL);
    }
    
    @GetMapping //요청 보내기
    public String getRecommendation(int memId, int n) {
    	String requestUrl = FLASK_API_URL + "?mem_id=" + memId + "&n=" + n;
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(requestUrl, String.class);
        String jsonResponse = responseEntity.getBody();

        try {
            // JSON 응답 파싱
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonResponse);

            // 첫 번째 추천 도서의 image_url 가져오기
            JsonNode recommendations = rootNode.path("recommendations");
            if (recommendations.isArray() && recommendations.size() > 0) {
                String imageUrl = recommendations.get(0).path("image_url").asText();
                return imageUrl;
            } else {
                return "No recommendations found";
            }
        } catch (Exception e) {
            return "Error parsing response: " + jsonResponse;
        }
    }
    @PostMapping //요청 받기
    public String postRecommendation(int memId, int n) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("mem_id", memId);
        requestBody.put("n", n);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(FLASK_API_URL, HttpMethod.POST, requestEntity, String.class);
        return response.getBody();
 }
    //Rest API
    @PutMapping //추천 도서 업데이트
    public String updateRecommendation(int memId, int n) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("mem_id", memId);
        requestBody.put("n", n);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(FLASK_API_URL, HttpMethod.PUT, requestEntity, String.class);
        return response.getBody();
    }
    @DeleteMapping//추천 도서 삭제
    public String deleteRecommendation(int memId) {
        String requestUrl = FLASK_API_URL + "?mem_id=" + memId;
        ResponseEntity<String> response = restTemplate.exchange(requestUrl, HttpMethod.DELETE, null, String.class);
        return response.getBody();
    }
}