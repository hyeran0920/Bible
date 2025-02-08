package com.library.bible.FlaskApi;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;

@Service
public class FlaskClientService {
    
    private final String FLASK_API_URL; // Flask의 Ngrok URL
    private final RestTemplate restTemplate = new RestTemplate();
    
    public FlaskClientService() {
        this.FLASK_API_URL = getNgrokUrl() + "/recommend";
        System.out.println("🚀 최종 Flask API URL: " + this.FLASK_API_URL);//
    }
   
    private String getNgrokUrl() {
        String ngrokApiUrl = "http://127.0.0.1:4040/api/tunnels";
        try {
        	System.out.println("🔍 Ngrok API 호출: " + ngrokApiUrl); //ss
            ResponseEntity<Map> response = restTemplate.getForEntity(ngrokApiUrl, Map.class);
            System.out.println("📡 응답 데이터: " + response.getBody());//ss
            if (response.getBody() != null) {
                for (Object tunnel : (Iterable<?>) response.getBody().get("tunnels")) {
                    Map<String, String> tunnelInfo = (Map<String, String>) tunnel;
                    if ("https".equals(tunnelInfo.get("proto"))) {
                    	String ngrokUrl = tunnelInfo.get("public_url");
                        System.out.println("✅ 발견된 Ngrok HTTPS URL: " + ngrokUrl);
                        return ngrokUrl;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "http://127.0.0.1:5000"; // ngrok이 실행되지 않았을 경우 기본 로컬 URL
    }
    @GetMapping
    public String getRecommendation(int memId, int n) {
        String requestUrl = FLASK_API_URL + "?mem_id=" + memId + "&n=" + n;
        return restTemplate.getForObject(requestUrl, String.class);
    }
    @PostMapping
    public String postRecommendation(int memId, int n) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("mem_id", memId);
        requestBody.put("n", n);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(FLASK_API_URL, HttpMethod.POST, requestEntity, String.class);
        return response.getBody();
//    private final String FLASK_API_URL = "http://127.0.0.1:5000/recommend"; // Flask의 Ngrok URL
//
//    public String getRecommendation(int userId, int n) {
//        RestTemplate restTemplate = new RestTemplate();
//        
//        String requestUrl = FLASK_API_URL + "?user_id=" + userId + "&n=" + n;
//        
//        // JSON 데이터 생성
//        //Map<String, Integer> requestData = new HashMap<>();
//        //requestData.put("input", inputValue);
//        return restTemplate.getForObject(FLASK_API_URL, String.class, userId, 10);
 }
}