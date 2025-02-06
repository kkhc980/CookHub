package com.dishcovery.project.persistence;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.dishcovery.project.domain.ReviewAttachVO;

@Mapper
public interface ReviewAttachMapper {
	int attachInsert(ReviewAttachVO reviewAttachVO);
	List<ReviewAttachVO> selectByRecipeReviewId(int recipeReviewId);
	ReviewAttachVO selectByAttachId (int attachId);
	int insertModify(ReviewAttachVO reviewAttachVO);
	int attachDelete(int recipeReviewId);
	List<ReviewAttachVO> selectOldList();
}
