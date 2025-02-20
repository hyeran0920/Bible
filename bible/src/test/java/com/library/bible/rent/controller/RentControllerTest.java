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
//import java.util.List;
//import java.util.Optional;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Disabled;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import com.library.bible.pageresponse.PageResponse;
//import com.library.bible.rent.dto.RentPageResponse;
//import com.library.bible.rent.model.RentStatus;
//import com.library.bible.rent.service.IRentService;
//
//class RentControllerTest {
//
//    private MockMvc mockMvc;
//
//    @InjectMocks
//    private RentController rentController;
//
//    @Mock
//    private IRentService rentService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        mockMvc = MockMvcBuilders.standaloneSetup(rentController).build();
//    }
//    @Test
//    @DisplayName("회원 ID로 대여 목록 조회 테스트")
//    void testGetRentsByMemberId() throws Exception {
//        PageResponse<RentPageResponse> pageResponse = new PageResponse<>(
//            Arrays.asList(new RentPageResponse()), // RentStatus 수정
//            1, 1, true
//        );
//
//        when(rentService.selectRentResponses(101L, Optional.empty(), PageRequest.of(0, 10)))
//            .thenReturn(pageResponse);
//
//        mockMvc.perform(get("/api/rents/me")
//                .param("page", "0")
//                .param("size", "10")
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.totalPages").value(1))
//                .andExpect(jsonPath("$.content[0].rentId").value(1L));
//    }
//    @Test
//    @DisplayName("도서 대여 생성 테스트")
//    void testCreateRent() throws Exception {
//        RentPageResponse rentResponse = new RentPageResponse();
//
//        when(rentService.insertRequestRents(any(Long.class), any(List.class), any(RentStatus.class)))
//        .thenReturn(List.of(rentResponse));
//
//
//        mockMvc.perform(post("/api/rents")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content("{\"memId\": 101, \"bookId\": 201, \"rentStatus\": \"REQUESTED\"}"))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$[0].rentId").value(1L))
//                .andExpect(jsonPath("$[0].rentStatus").value("REQUESTED"));
//    }
//
//    @Test
//    @DisplayName("대여 정보 삭제 테스트")
//    void testDeleteRent() throws Exception {
//    	doNothing().when(rentService).deleteRent(1L);
// // void 메서드라 doNothing 사용
//
//        mockMvc.perform(delete("/api/rents/1"))
//                .andExpect(status().isNoContent());
//    }
//}
