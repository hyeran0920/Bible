package com.library.bible.FlaskApi;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FlaskClientServiceTest {

    @Autowired
    private FlaskClientService flaskClientService;

    @Test
    void testFlaskApiCall() {
        // Flask API에서 응답을 받아오는 메서드를 호출 //
        String response = flaskClientService.getRecommendation(1030, 10);
        
        System.out.println("Flask API 응답: " + response);
        assertNotNull(response, "not Null.");
        assertTrue(response.startsWith("http"), "유효한 URL이 아닙니다.");   
    }
}