package com.library.bible.review.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.library.bible.review.model.Review;
import com.library.bible.review.service.IReviewService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/reviews")
public class ReviewController {
	
	@Autowired
	IReviewService reviewService; // 소문자로 변경
	
	@GetMapping
	public List<Review> getReview() {
		System.out.println("get review all");
		return reviewService.getReview();
	}
	
	@PostMapping
    public ResponseEntity<String> addReview(@AuthMember Member member, @RequestBody ReviewRequest request) {
        
        long memId = member.getMemId();  // 인증된 사용자 ID 가져오기
        long bookId = request.getBookId();  // 요청에서 책 ID 가져오기
        
        // 리뷰 추가
        try {
            reviewService.insertReview(memId, bookId, request.getReviewStar(), request.getReviewComment());
            return ResponseEntity.ok("리뷰가 성공적으로 추가되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("리뷰 추가에 실패했습니다.");
        }
    }
}