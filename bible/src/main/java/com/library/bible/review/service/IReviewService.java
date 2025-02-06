package com.library.bible.review.service;

import java.util.List;
import com.library.bible.member.model.Member;
import com.library.bible.resolver.AuthMember;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.library.bible.review.model.Review;

public interface IReviewService {

	List<Review> getReview();
    List<Review> getBookReview(long bookId);
    List<Review> getMemberReview(long memId);

	void insertReview(long memId, Review review);

	void deleteReview(long memId, long reviewId);
	void deleteAdminReview(long reviewId);


}
