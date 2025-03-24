package com.dishcovery.project.service;

import java.util.List;

import com.dishcovery.project.domain.RecipeReviewDTO;
import com.dishcovery.project.util.Pagination;

public interface RecipeReviewService {
	
	List<RecipeReviewDTO> getAllRecipeReview(int recipeBoardId, Pagination pagination); // pagination추가
	int getTotalReviewCount(int recipeBoardId); // 총 리뷰 수 메서드 추가
	int createRecipeReview(RecipeReviewDTO recipeReviewDTO);
	int updateRecipeReview(RecipeReviewDTO recipeReviewDTO);
	int deleteRecipeReview(int recipeReviewId, int recipeBoardId);
	RecipeReviewDTO getReviewById(int recipeReviewId);
}
	
	

