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
	// transactionManage媛� 愿�由�
	
	@Override
	public int createRecipeReview(RecipeReviewVO recipeReviewVO) {
		// 由щ럭瑜� 異붽��븯硫�
		// RecipeReview �뀒�씠釉붿씠 �뙎湲��뿉 �벑濡�
		log.info("CreateRecipeReview()");
		int insertResult = recipeReviewMapper.insertRecipeReview(recipeReviewVO);
		log.info(insertResult + "�뻾 由щ럭 異붽�");
//		int updateResult = recipeBoardMapper
//				.updateRecipeReviewCount(recipeReviewVO.getRecipeBoardId(), 1);
//		log.info(updateResult + "�뻾 寃뚯떆�뙋 �닔�젙");
		// board �뀒�씠釉붿뿉 updateRecipeReviewCount 而щ읆 異붽� <- 由щ럭 媛��닔 湲곕줉
		
		return 1;
				
	}
	
	@Override
	public List<RecipeReviewVO> getAllRecipeReview(int recipeBoardId) {
		log.info("getAllRecipeReview()");
		return recipeReviewMapper.selectListByRecipeBoardId(recipeBoardId);
	}
	// RecipeReview瑜� recipeBoardId濡� 遺덈윭�샂
	
	@Override
	public int updateRecipeReview(RecipeReviewVO recipeReviewVO) {
		log.info("recipeReviewVO");
		return recipeReviewMapper.updateRecipeReview(recipeReviewVO);
	}
	
	@Transactional(value = "transactionManager")
	
	@Override
	public int deleteRecipeReview(int recipeReviewId, int recipeBoardId) {
		log.info("deleteRecipeReview()");
		int deleteResult = recipeReviewMapper.deleteRecipeReview(recipeReviewId);
		log.info(deleteResult + "�뻾 由щ럭 �궘�젣");
//		int updateResult = recipeBoardMapper
//				.updateRecipeReviewCount(recipeBoardId, -1);
//		log.info(updateResult + "�뻾 寃뚯떆�뙋 �닔�젙");
		// board �뀒�씠釉� updateRecipeReviewCount - 1 �궘�젣
		
		return 1;
	}
	
}
