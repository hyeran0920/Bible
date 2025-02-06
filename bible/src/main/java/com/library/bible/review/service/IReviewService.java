package com.library.bible.review.service;

import java.util.List;
import java.util.Map;

import com.library.bible.review.model.Review;

public interface IReviewService {

	List<Review> getReview();
    List<Review> getBookReview(long bookId);

    void insertReview(long memId, long bookId, int reviewstar, String reviewComment);
}
