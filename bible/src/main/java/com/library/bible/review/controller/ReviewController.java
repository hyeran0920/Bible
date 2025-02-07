package com.library.bible.review.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.library.bible.review.model.Review;
import com.library.bible.review.service.IReviewService;
import com.library.bible.cart.model.Cart;
import com.library.bible.member.model.Member;
import com.library.bible.resolver.AuthMember;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/reviews")
public class ReviewController {
	
	@Autowired
	IReviewService reviewService; // 소문자로 변경
	
	@GetMapping("/admin")
	public List<Review> getReview() {
		System.out.println("get review all");
		return reviewService.getReview();
	}
	
	// 특정 책 리뷰 데이터 조회
	@GetMapping("/{bookId}")
	public List<Review> getBookReview(@PathVariable long bookId){
		return reviewService.getBookReview(bookId);
	}
	// 특정 사용자 리뷰 데이터 조회
	@GetMapping("/me")
    public ResponseEntity<List<Review>> getMembereview(@AuthMember Member member) {
        List<Review> reviewList = reviewService.getMemberReview(member.getMemId());
        return ResponseEntity.ok(reviewList);
    }
	
	//리뷰 추가
	@PostMapping
    public ResponseEntity<String> addReview(@AuthMember Member member, @RequestBody Review request) {
        System.out.println("add review");    
		reviewService.insertReview(member.getMemId(),request);
        return ResponseEntity.ok("리뷰가 성공적으로 추가되었습니다.");

    }
	// 리뷰 삭제 (memId와 reviewId 사용)
	@PostMapping("/{reviewId}")
	public ResponseEntity<String> delReview(@PathVariable long reviewId, @AuthMember Member member) {
	    reviewService.deleteReview(reviewId, member.getMemId());
	    return ResponseEntity.ok("리뷰가 삭제되었습니다");
	}
	
	//Admin Review Delete
	@PostMapping("/admin/{reviewId}")
	public ResponseEntity<String> delAdmin(@PathVariable long reviewId){
		reviewService.deleteAdminReview(reviewId);
		return ResponseEntity.ok("관리자 리뷰 삭제");
	}

	
}