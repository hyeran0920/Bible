//package com.library.bible.rent.controller;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.doNothing;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//import java.util.Arrays;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import com.library.bible.rent.dto.RentRequest;
//import com.library.bible.rent.dto.RentResponse;
//import com.library.bible.rent.service.RentService;
//
//class RentControllerTest {
//
//    private MockMvc mockMvc;
//
//    @InjectMocks
//    private RentController rentController;
//
//    @Mock
//    private RentService rentService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        mockMvc = MockMvcBuilders.standaloneSetup(rentController).build();
//    }
//
//    @Test
//    void testGetRentsByMemberId() throws Exception {
//        RentResponse response1 = new RentResponse(1L, 101L, 201L, "Active");
//        RentResponse response2 = new RentResponse(2L, 102L, 202L, "Returned");
//
//        when(rentService.getRentsByMemberId(101L)).thenReturn(Arrays.asList(response1, response2));
//
//        mockMvc.perform(get("/rents/member/101")
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.length()").value(2))
//                .andExpect(jsonPath("$[0].rentId").value(1L))
//                .andExpect(jsonPath("$[0].status").value("Active"));
//    }
//
//    @Test
//    void testCreateRent() throws Exception {
//        RentRequest request = new RentRequest(101L, 201L);
//        RentResponse response = new RentResponse(1L, 101L, 201L, "Active");
//
//        when(rentService.createRent(any(RentRequest.class))).thenReturn(response);
//
//        mockMvc.perform(post("/rents")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content("{\"memberId\": 101, \"bookId\": 201}"))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.rentId").value(1L))
//                .andExpect(jsonPath("$.status").value("Active"));
//    }
//
//    @Test
//    void testDeleteRent() throws Exception {
//        doNothing().when(rentService).deleteRent(1L);
//
//        mockMvc.perform(delete("/rents/1"))
//                .andExpect(stat
//    }