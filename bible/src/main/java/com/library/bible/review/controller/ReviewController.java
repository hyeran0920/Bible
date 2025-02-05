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
import org.springframework.web.bind.annotation.RestController;

import com.library.bible.review.model.Review;
import com.library.bible.review.service.IReviewService;
import com.library.bible.member.model.Member;
import com.library.bible.resolver.AuthMember;
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
	
	// 특정 책 리뷰 데이터 조회
	@GetMapping("/{bookId}")
	public List<Review> getBookReview(@PathVariable long bookId){
		return reviewService.getBookReview(bookId);
	}
	
	
	@PostMapping
    public ResponseEntity<String> addReview(@AuthMember Member member, @RequestBody Review request) {
        
        long memId = member.getMemId();  // 인증된 사용자 ID 가져오기
        long bookId = request.getBookId();  // 요청에서 책 ID 가져오기
        int reviewStar = request.getReviewStar();
        String reviewComment = request.getReviewComment();
        
        // 리뷰 추가
            reviewService.insertReview(memId, bookId, reviewStar,reviewComment);
            return ResponseEntity.ok("리뷰가 성공적으로 추가되었습니다.");

    }
}