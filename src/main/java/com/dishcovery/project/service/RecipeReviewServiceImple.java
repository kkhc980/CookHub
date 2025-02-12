package com.dishcovery.project.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dishcovery.project.domain.RecipeReviewDTO;
import com.dishcovery.project.domain.RecipeReviewVO;
import com.dishcovery.project.domain.ReviewAttachDTO;
import com.dishcovery.project.domain.ReviewAttachVO;
import com.dishcovery.project.persistence.RecipeReviewMapper;
import com.dishcovery.project.persistence.ReviewAttachMapper;

import lombok.extern.log4j.Log4j;

@Service
@Log4j
public class RecipeReviewServiceImple implements RecipeReviewService{
   
   @Autowired
   private RecipeReviewMapper recipeReviewMapper;
   
   @Autowired
   private ReviewAttachMapper reviewAttachMapper;
   
   @Transactional(value = "transactionManager")   
   @Override
   public int createRecipeReview(RecipeReviewDTO recipeReviewDTO) {
      log.info("CreateRecipeReview()");
      log.info("recipeReviewDTO = " + recipeReviewDTO);
      int insertRecipeReviewResult = recipeReviewMapper.insertRecipeReview(toEntity(recipeReviewDTO));
      log.info(insertRecipeReviewResult + "행 리뷰 등록");
      
      
      List<ReviewAttachDTO> reviewAttachList = recipeReviewDTO.getReviewAttachList();
      
      int insertReviewAttachResult = 0;
      
      for(ReviewAttachDTO reviewAttachDTO : reviewAttachList) {
         insertRecipeReviewResult += reviewAttachMapper.attachInsert(toEntity(reviewAttachDTO));
      }
      log.info(insertReviewAttachResult + "행 파일 정보 등록");
      
      return 1;
   } // createRecipeReview
   
   @Override
   public List<RecipeReviewDTO> getAllRecipeReview(int recipeBoardId) {
      log.info("getAllRecipeReview()");
      List<RecipeReviewVO> list = recipeReviewMapper.selectListByRecipeBoardId(recipeBoardId);
      
      return list.stream().map(this::toDTO).collect(Collectors.toList());
   } // end getAllRecipeReview
   
   
   
   @Override
   @Transactional(value = "transactionManager")
   public int updateRecipeReview(RecipeReviewDTO recipeReviewDTO) {
      log.info("updateRecipeReview()");
      log.info("recipeReviewDTO = " + recipeReviewDTO);
      int updateRecipeReviewResult = recipeReviewMapper.updateRecipeReview(toEntity(recipeReviewDTO));
      log.info(updateRecipeReviewResult + "행 리뷰 수정");
      
      int deleteReviewAttachResult = reviewAttachMapper.attachDelete(recipeReviewDTO.getRecipeReviewId());
      log.info(deleteReviewAttachResult + "행 이미지 삭제");
      
      List<ReviewAttachDTO> reviewAttachList = recipeReviewDTO.getReviewAttachList();
      
      int insertReviewAttachResult = 0;
      for(ReviewAttachDTO reviewAttachDTO : reviewAttachList) {
         reviewAttachDTO.setRecipeReviewId(recipeReviewDTO.getRecipeReviewId());
         insertReviewAttachResult += reviewAttachMapper.insertModify(toEntity(reviewAttachDTO));
      }
      log.info(insertReviewAttachResult + "행 이미지 정보 등록");
      return 1;
   } // end updateRecipeReview()
   
   @Transactional(value = "transactionManager")
   @Override
   public int deleteRecipeReview(int recipeReviewId, int recipeBoardId) {
      log.info("deleteRecipeReview()");
      log.info("recipeReviewId = " + recipeReviewId);
      
      int deleteReviewAttachResult = reviewAttachMapper.attachDelete(recipeReviewId);
      log.info(deleteReviewAttachResult + "행 이미지 정보 삭제");
      
      int deleteRecipeReviewResult = recipeReviewMapper.deleteRecipeReview(recipeReviewId);
      log.info(deleteRecipeReviewResult + "행 리뷰 정보 삭제");
      
//      int updateResult = recipeBoardMapper
//            .updateRecipeReviewCount(recipeBoardId, -1);
//      log.info(updateResult + " 뻾 寃뚯떆 뙋  닔 젙");
      // board  뀒 씠釉  updateRecipeReviewCount - 1  궘 젣
      
      return 1;
   }
   
   // RecipeReview 데이터를 RecipeReviewDTO에 적용하는 메서드
   public RecipeReviewDTO toDTO(RecipeReviewVO recipeReview) {
      RecipeReviewDTO recipeReviewDTO = new RecipeReviewDTO();
      recipeReviewDTO.setRecipeReviewId(recipeReview.getRecipeReviewId());
      recipeReviewDTO.setRecipeReviewContent(recipeReview.getRecipeReviewContent());
      recipeReviewDTO.setMemberId(recipeReview.getMemberId());
      recipeReviewDTO.setReviewRating(recipeReview.getReviewRating());
      recipeReviewDTO.setRecipeReviewDateCreated(recipeReview.getRecipeReviewDateCreated());
      recipeReviewDTO.setRecipeBoardId(recipeReview.getRecipeBoardId());
      
      // 이미지 정보도 DTO로 변환하여 추가
       if (recipeReview.getReviewAttachList() != null) {
           recipeReviewDTO.setReviewAttachList(recipeReview.getReviewAttachList().stream()
               .map(this::toDTO) // ReviewAttachVO → ReviewAttachDTO 변환
               .collect(Collectors.toList()));
       }
      
      return recipeReviewDTO;
   } // end toDTO()
   
   // RecipeReviewDTO 데이터를 RecipeReview에 적용하는 메서드
   public RecipeReviewVO toEntity(RecipeReviewDTO recipeReviewDTO) {
      RecipeReviewVO recipeReview = new RecipeReviewVO();
      recipeReview.setRecipeReviewId(recipeReviewDTO.getRecipeReviewId());
      recipeReview.setRecipeReviewContent(recipeReviewDTO.getRecipeReviewContent());
      recipeReview.setMemberId(recipeReviewDTO.getMemberId());
      recipeReview.setReviewRating(recipeReviewDTO.getReviewRating());
      recipeReview.setRecipeBoardId(recipeReviewDTO.getRecipeBoardId());
      return recipeReview;
   }
   
   private ReviewAttachDTO toDTO(ReviewAttachVO reviewAttach) {
      ReviewAttachDTO reviewAttachDTO = new ReviewAttachDTO();
      reviewAttachDTO.setAttachId(reviewAttach.getAttachId());
      reviewAttachDTO.setRecipeReviewId(reviewAttach.getRecipeReviewId());
      reviewAttachDTO.setAttachPath(reviewAttach.getAttachPath());
      reviewAttachDTO.setAttachRealName(reviewAttach.getAttachRealName());
      reviewAttachDTO.setAttachChgName(reviewAttach.getAttachChgName());
      reviewAttachDTO.setAttachExtension(reviewAttach.getAttachExtension());
      reviewAttachDTO.setAttachDateCreated(reviewAttach.getAttachDateCreated());
      return reviewAttachDTO;
   }
   
   private ReviewAttachVO toEntity(ReviewAttachDTO reviewAttachDTO) {
      ReviewAttachVO reviewAttach = new ReviewAttachVO();
      reviewAttach.setAttachId(reviewAttachDTO.getAttachId());
      reviewAttach.setRecipeReviewId(reviewAttachDTO.getRecipeReviewId());
      reviewAttach.setAttachPath(reviewAttachDTO.getAttachPath());
      reviewAttach.setAttachRealName(reviewAttachDTO.getAttachRealName());
      reviewAttach.setAttachChgName(reviewAttachDTO.getAttachChgName());
      reviewAttach.setAttachExtension(reviewAttachDTO.getAttachExtension());
      reviewAttach.setAttachDateCreated(reviewAttachDTO.getAttachDateCreated());
      return reviewAttach;
   }
   
}
