package com.dishcovery.project.domain;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class RecipeBoardDTO {
	private int recipeBoardId;
	private String recipeBoardTitle;
	private String recipeBoardContent;
	private int memberId;
	private Date recipeBoardCreatedDate;
	private int viewCount;
	private String typeName;
	private String methodName;
	private String situationName;
	private double avgRating;
	private int replyCount;
	private int recipeReviewCount;
	private String thumbnailPath;
	
	String servings;
	String time;
	String difficulty;
	String recipeTip;
	
	private List<RecipeBoardStepVO> recipeSteps;
	private List<RecipeIngredientsDetailVO> ingredientDetails;
	private List<HashtagsVO> hashtags; // String 대신 List<HashtagsVO> 사용
	private List<IngredientsVO> ingredients; // 재료 리스트

}
