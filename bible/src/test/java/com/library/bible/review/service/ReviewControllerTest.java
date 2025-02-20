package com.library.bible.review.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import com.library.bible.review.model.Review;
import com.library.bible.review.repository.IReviewRepository;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test") // 테스트 환경에서만 실행
class ReviewServiceTest {

    @Mock
    private IReviewRepository reviewRepository;

    @InjectMocks
    private ReviewService reviewService;

    private Review review;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        review = new Review();
        review.setReviewId(1L);
        review.setReviewStar(5);
        review.setReviewComment("Great book!");
        review.setBookId(100L);
        review.setMemId(200L);
    }

    @Test
    void testGetReview() {
        when(reviewRepository.getReview()).thenReturn(Arrays.asList(review));

        List<Review> reviews = reviewService.getReview();
        assertThat(reviews).hasSize(1);
        verify(reviewRepository, times(1)).getReview();
    }

    @Test
    void testInsertReview() {
        reviewService.insertReview(200L, review);
        verify(reviewRepository, times(1)).insertReview(review);
    }
}
