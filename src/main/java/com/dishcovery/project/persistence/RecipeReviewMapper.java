package com.dishcovery.project.persistence;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.dishcovery.project.domain.RecipeReviewVO;

@Mapper
public interface RecipeReviewMapper {
	int insertRecipeReview(RecipeReviewVO recipeReviewVO);
	List<RecipeReviewVO> selectListByRecipeBoardId(int recipeBoardId);
	int updateRecipeReview(RecipeReviewVO recipeReviewVO);
	int deleteRecipeReview(int RecipeReviewId);
}
