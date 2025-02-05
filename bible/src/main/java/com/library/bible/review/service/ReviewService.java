package com.library.bible.review.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.library.bible.review.model.Review;
import com.library.bible.review.repository.IReviewRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ReviewService implements IReviewService{
	
	@Autowired
	IReviewRepository reviewRepository;

	@Override
	public List<Review> getReview() {
		List<Review> reviews=null;
		try {
			 reviews = reviewRepository.getReview();
		}catch(Exception e) {
			System.out.println("Unexpected error: " + e);
		}
	   
	    
	    if (reviews == null || reviews.isEmpty()) {
	        System.out.println("No reviews found or null returned from repository");
	    } else {
	        System.out.println("Retrieved " + reviews.size() + " reviews");
	    }
	    return reviews;
	}

	@Override
	public void insertReview(long memId, long bookId) {
		reviewRepository.insertReview(memId, bookId);
	}




}
