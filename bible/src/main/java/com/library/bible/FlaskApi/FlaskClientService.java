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

    public FlaskClientService() {
        System.out.println("Flask API URL: " + this.FLASK_API_URL);
    }
     //요청 보내기
    public String getRecommendation(int memId, int n) {
        String requestUrl = FLASK_API_URL + "?mem_id=" + memId + "&n=" + n;
        System.out.println("📢 Sending GET request to Flask: " + requestUrl);
        return restTemplate.getForObject(requestUrl, String.class);
    } 
    //요청 받기
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
    //추천 도서 업데이트
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
    //추천 도서 삭제
    public String deleteRecommendation(int memId) {
        String requestUrl = FLASK_API_URL + "?mem_id=" + memId;
        ResponseEntity<String> response = restTemplate.exchange(requestUrl, HttpMethod.DELETE, null, String.class);
        return response.getBody();
    }
}