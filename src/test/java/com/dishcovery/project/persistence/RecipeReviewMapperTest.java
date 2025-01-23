package com.dishcovery.project.persistence;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.dishcovery.project.config.RootConfig;
import com.dishcovery.project.domain.RecipeReviewVO;

import lombok.extern.log4j.Log4j;

@RunWith(SpringJUnit4ClassRunner.class) // 스프링 JUnit test 연결
@ContextConfiguration(classes = { RootConfig.class }) // 설정 파일 연결
@Log4j
public class RecipeReviewMapperTest {
	
	@Autowired
	private RecipeReviewMapper recipeReviewMapper;
	
	@Test
	public void test() {
	  	 insertRecipeReview();
	   }

	private void insertRecipeReview() {
		RecipeReviewVO recipeReviewVO = new RecipeReviewVO();
		recipeReviewVO.setRecipeBoardId(1);
		recipeReviewVO.setRecipeReviewContent("리뷰 테스트 123");
		recipeReviewVO.setMemberId(1);
		recipeReviewVO.setReviewRating(3);
		int result = recipeReviewMapper.insertRecipeReview(recipeReviewVO);
		log.info(recipeReviewVO);
	   }
	
//	public void test() {
//		 updateRecipeReview();
//	   }
//	
//	private void updateRecipeReview() {
//		RecipeReviewVO recipeReviewVO = new RecipeReviewVO();
//		recipeReviewVO.setRecipeReviewId(2);
//		recipeReviewVO.setRecipeReviewContent("리뷰 수정 테스트");
//		recipeReviewVO.setMemberId(1);
//		recipeReviewVO.setReviewRating(4);
//		int result = recipeReviewMapper.updateRecipeReview(recipeReviewVO);
//		log.info(recipeReviewVO);
//	   }
	
//	public void test() {
//		deleteRecipeReview();
//	}
//	
//	private void deleteRecipeReview() {
//		RecipeReviewVO recipeReviewVO = new RecipeReviewVO();
//		recipeReviewVO.setRecipeReviewContent("댓글 삭제 테스트");
//		int result = recipeReviewMapper.deleteRecipeReview(16);
//		log.info(recipeReviewVO);
//	}
}
