package com.dishcovery.project.service;

import java.util.List;

import com.dishcovery.project.domain.RecipeReviewDTO;

public interface RecipeReviewService {
	
	int createRecipeReview(RecipeReviewDTO recipeReviewDTO);
	List<RecipeReviewDTO> getAllRecipeReview(int recipeBoardId);
	int updateRecipeReview(RecipeReviewDTO recipeReviewDTO);
	int deleteRecipeReview(int recipeReviewId, int recipeBoardId);    
    
}
	
	

