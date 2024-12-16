package com.dishcovery.project.persistence;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.dishcovery.project.domain.RecipeReviewVO;

@Mapper
public interface RecipeReviewMapper {
	int insert(RecipeReviewVO recipeReviewVO);
	List<RecipeReviewVO> selectListByBoardId(int boardId);
	int update(RecipeReviewVO recipeReviewVO);
	int delete(int RecipeReviewId);
}
