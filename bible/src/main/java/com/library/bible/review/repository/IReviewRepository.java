package com.library.bible.review.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.library.bible.review.model.Review;

@Mapper
public interface IReviewRepository {
    List<Review> getReview();
    void insertReview(long memId, long bookId);
}
