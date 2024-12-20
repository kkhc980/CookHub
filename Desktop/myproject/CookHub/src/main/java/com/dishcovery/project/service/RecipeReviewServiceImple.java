package com.dishcovery.project.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dishcovery.project.domain.RecipeReviewVO;
import com.dishcovery.project.persistence.RecipeBoardMapper;
import com.dishcovery.project.persistence.RecipeReviewMapper;

import lombok.extern.log4j.Log4j;

@Service
@Log4j
public class RecipeReviewServiceImple implements RecipeReviewService{
	
	@Autowired
	private RecipeReviewMapper recipeReviewMapper;
	
	@Autowired
	private RecipeBoardMapper recipeBoardMapper;
	
	
	@Transactional(value = "transactionManager")
	// transactionManage가 관리
	
	@Override
	public int createRecipeReview(RecipeReviewVO recipeReviewVO) {
		// 리뷰를 추가하면
		// RecipeReview 테이블이 댓글에 등록
		log.info("CreateRecipeReview()");
		int insertResult = recipeReviewMapper.insertRecipeReview(recipeReviewVO);
		log.info(insertResult + "행 리뷰 추가");
//		int updateResult = recipeBoardMapper
//				.updateRecipeReviewCount(recipeReviewVO.getRecipeBoardId(), 1);
//		log.info(updateResult + "행 게시판 수정");
		// board 테이블에 updateRecipeReviewCount 컬럼 추가 <- 리뷰 갯수 기록
		
		return 1;
				
	}
	
	@Override
	public List<RecipeReviewVO> getAllRecipeReview(int recipeBoardId) {
		log.info("getAllRecipeReview()");
		return recipeReviewMapper.selectListByRecipeBoardId(recipeBoardId);
	}
	// RecipeReview를 recipeBoardId로 불러옴
	
	@Override
	public int updateRecipeReview(int recipeReviewId, String recipeReviewContent) {
		log.info("recipeReviewContent");
		RecipeReviewVO recipeReviewVO = new RecipeReviewVO();
		recipeReviewVO.setRecipeReviewId(recipeReviewId);
		recipeReviewVO.setRecipeReviewContent(recipeReviewContent);
		recipeReviewVO.setReviewRating(recipeReviewId);
		return recipeReviewMapper.updateRecipeReview(recipeReviewVO);
	}
	
	@Transactional(value = "transactionManager")
	
	@Override
	public int deleteRecipeReview(int recipeReviewId, int recipeBoardId) {
		log.info("deleteRecipeReview()");
		int deleteResult = recipeReviewMapper.deleteRecipeReview(recipeReviewId);
		log.info(deleteResult + "행 리뷰 삭제");
//		int updateResult = recipeBoardMapper
//				.updateRecipeReviewCount(recipeBoardId, -1);
//		log.info(updateResult + "행 게시판 수정");
		// board 테이블 updateRecipeReviewCount - 1 삭제
		
		return 1;
	}
	
}
