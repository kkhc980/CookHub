package com.dishcovery.project.domain;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class RecipeReviewVO {
	private int recipeReviewId;
	private int boardId;
	private String memberId;
	private String reviewContent;
	private int reviewRating;
	private Date recipeReviewDateCreated;
	
}