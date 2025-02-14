//package com.library.bible.Flask;
//
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//import java.util.Map;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.context.annotation.Import;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.web.cors.CorsConfiguration;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.library.bible.FlaskApi.FlaskClientService;
//import com.library.bible.FlaskApi.FlaskController;
//import com.library.bible.config.CorsConfig;
//
//@ExtendWith(MockitoExtension.class) // Mockito 확장 기능 사용
//@WebMvcTest(controllers = FlaskController.class)
//@Import(CorsConfig.class) // CorsConfig를 명시적으로 포함
//public class FlaskControllerTest {
//
//    private MockMvc mockMvc;
//
//    @Mock
//    private FlaskClientService flaskClientService; // Flask 연동 서비스 Mock 처리
//
//    @Mock
//    private CorsConfiguration corsConfiguration; // CORS 설정을 Mocking
//
//    @InjectMocks
//    private FlaskController flaskController;
//
//    @BeforeEach
//    void setup() {
//        MockitoAnnotations.openMocks(this);
//        mockMvc = MockMvcBuilders.standaloneSetup(flaskController).build();
//    }
//
//    @Test
//    public void testGetFlaskRecommendation() throws Exception {
//        int memId = 1030;
//        int n = 5;
//
//        when(flaskClientService.getRecommendation(memId, n)).thenReturn("Recommendation Response");
//
//        mockMvc.perform(get("/flask/recommend")
//                .param("mem_id", String.valueOf(memId))
//                .param("n", String.valueOf(n)))
//                .andExpect(status().isOk());
//    }
//
////    @Test
////    public void testPostFlaskRecommendation() throws Exception {
////        int memId = 1030;
////        int n = 5;
////
////        // 요청 데이터 생성
////        Map<String, Object> requestBody = Map.of("mem_id", memId, "n", n);
////        String jsonBody = new ObjectMapper().writeValueAsString(requestBody);
////
////        when(flaskClientService.postRecommendation(memId, n)).thenReturn("Post Recommendation Response");
////
////        mockMvc.perform(post("/flask/recommend")
////                .contentType(MediaType.APPLICATION_JSON)
////                .content(jsonBody))
////                .andExpect(status().isOk());
////    }
//}
