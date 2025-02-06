package com.library.bible.review.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.library.bible.review.model.Review;

@Mapper
public interface IReviewRepository {
    List<Review> getReview();
    List<Review> getBookReview(long bookId);
    
    
    void insertReview(long memId, long bookId, int reviewstar, String reviewComment);
    
    
}
