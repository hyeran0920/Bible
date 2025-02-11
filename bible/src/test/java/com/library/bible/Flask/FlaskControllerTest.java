package com.library.bible.Flask;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.library.bible.FlaskApi.FlaskClientService;
import com.library.bible.FlaskApi.FlaskController;

@WebMvcTest(FlaskController.class)
class FlaskControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    private FlaskClientService flaskClientService;

    @Test
    void testPostFlaskRecommendation() throws Exception {
        mockMvc.perform(post("/flask/recommend")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"mem_id\":1030,\"n\":5}"))
            .andExpect(status().isOk());
    }
}