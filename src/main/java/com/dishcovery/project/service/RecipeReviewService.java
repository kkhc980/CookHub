package com.dishcovery.project.service;

import java.util.List;

import com.dishcovery.project.domain.RecipeReviewVO;

public interface RecipeReviewService {
	int createRecipeReview(RecipeReviewVO recipeReviewVO);
	List<RecipeReviewVO> getAllRecipeReview(int recipeBoardId);
	int updateRecipeReview(int recipeReviewId, String recipeReviewContent);
	int deleteRecipeReview(int recipeReviewId, int recipeBoardId);
	
}
