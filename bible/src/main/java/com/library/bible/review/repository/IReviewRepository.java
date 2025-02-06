package com.library.bible.review.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.library.bible.member.model.Member;
import com.library.bible.resolver.AuthMember;
import com.library.bible.review.model.Review;

import com.library.bible.member.model.Member;
import com.library.bible.resolver.AuthMember;

@Mapper
public interface IReviewRepository {
    List<Review> getReview();
    List<Review> getBookReview(long bookId);
    
    
	void insertReview(@AuthMember Member member, Review review);
	void deleteReview(long memId, long reviewId);
	
	void deleteAdminReview(long reviewId);
    
    
}
