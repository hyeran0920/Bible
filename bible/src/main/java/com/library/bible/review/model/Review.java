package com.library.bible.review.model;

import java.sql.Timestamp;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Review {
	private Long reviewId;
	private Integer reviewStar;
	private String reviewComment;
	private Long bookId;
	private Long memId;
	private Timestamp createdAt;

}
