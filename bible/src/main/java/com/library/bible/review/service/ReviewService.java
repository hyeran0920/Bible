package com.library.bible.review.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.library.bible.review.model.Review;
import com.library.bible.review.repository.IReviewRepository;

import jakarta.transaction.Transactional;

import com.library.bible.member.model.Member;
import com.library.bible.resolver.AuthMember;
@Service
@Transactional
public class ReviewService implements IReviewService{
	
	@Autowired
	IReviewRepository reviewRepository;

	@Override
	public List<Review> getReview() {
	    return reviewRepository.getReview();
	}

	
	@Override
	public List<Review> getBookReview(long bookId) {
		return reviewRepository.getBookReview(bookId);
	}
	
	
	@Override
	public List<Review> getMemberReview(long memId) {
		return reviewRepository.getMemberReview(memId);
	}

	
	@Override
	public void insertReview(long memId, Review review) {
		review.setMemId(memId);
		reviewRepository.insertReview(review);
	}


	@Override
	public void deleteReview(long memId, long reviewId) {
		reviewRepository.deleteReview(memId, reviewId);
	}


	@Override
	public void deleteAdminReview(long reviewId) {
		reviewRepository.deleteAdminReview(reviewId);
	}




}
