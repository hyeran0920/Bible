/*
 * package com.library.bible.review.controller;
 * 
 * import com.library.bible.review.model.Review; import
 * com.library.bible.review.service.IReviewService; import
 * org.junit.jupiter.api.DisplayName; import org.junit.jupiter.api.Test; import
 * org.mockito.Mockito; import
 * org.springframework.beans.factory.annotation.Autowired; import
 * org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest; import
 * org.springframework.boot.test.mock.mockito.MockBean; import
 * org.springframework.http.MediaType; import
 * org.springframework.test.web.servlet.MockMvc;
 * 
 * import java.util.Arrays; import java.util.List;
 * 
 * import static org.mockito.ArgumentMatchers.anyLong; import static
 * org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
 * import static
 * org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
 * import static
 * org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
 * import static
 * org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
 * 
 * @WebMvcTest(ReviewController.class) public class ReviewControllerTest {
 * 
 * @Autowired private MockMvc mockMvc;
 * 
 * private IReviewService reviewService;
 * 
 * @Test
 * 
 * @DisplayName("리뷰 추가") void testAddReview() throws Exception {
 * mockMvc.perform(post("/api/reviews") .contentType(MediaType.APPLICATION_JSON)
 * .content("{\"content\": \"좋은 책입니다.\"}")) .andExpect(status().isOk()); } }
 */
