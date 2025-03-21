package com.dishcovery.project.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dishcovery.project.domain.CustomUser;
import com.dishcovery.project.domain.RecipeReviewDTO;
import com.dishcovery.project.service.RecipeBoardService;
import com.dishcovery.project.service.RecipeReviewService;
import com.dishcovery.project.util.PageMaker;
import com.dishcovery.project.util.Pagination;

import lombok.extern.log4j.Log4j;

@RestController
@RequestMapping(value = "/recipeboard")
@Log4j
public class RecipeReviewRESTController {
   
   @Autowired
   private RecipeReviewService recipeReviewService;
   
   @Autowired
   private RecipeBoardService recipeBoardService;  // 추가: RecipeBoardService 주입

   
   @PreAuthorize("hasRole('ROLE_MEMBER')")
   @PostMapping("/reviews/detail")
   public ResponseEntity<Integer> createRecipeReview(@RequestBody RecipeReviewDTO recipeReviewDTO) {
      log.info("createRecipeReview()");
      log.info("recipReviewDTO = " + recipeReviewDTO);
      
      int result = recipeReviewService.createRecipeReview(recipeReviewDTO);
      log.info(result + "리뷰 등록");
      
      // 리뷰 추가 후 별점 평균 갱신
      recipeBoardService.updateAverageRating(recipeReviewDTO.getRecipeBoardId());

      return new ResponseEntity<Integer>(result, HttpStatus.OK);
      
   }
   
   
   @GetMapping("/allReviews/{recipeBoardId}")
   public ResponseEntity<Map<String, Object>> readAllRecipeReview(
           @PathVariable("recipeBoardId") int recipeBoardId,
           @RequestParam(value = "pageNum", defaultValue = "1") int pageNum) {
	   
       Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
       Integer currentUserId = null;
       
       if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof CustomUser) {
           CustomUser customUser = (CustomUser) authentication.getPrincipal();
           currentUserId = customUser.getMemberVO().getMemberId();
       }
       
       log.info("현재 로그인한 사용자 ID = " + currentUserId);

       // ✅ Pagination 객체 생성 (한 페이지에 5개씩)
       Pagination pagination = new Pagination(pageNum, 5);

       // ✅ 전체 리뷰 개수 조회
       int reviewTotalCount = recipeReviewService.getTotalReviewCount(recipeBoardId);
       log.info("총 리뷰 개수 (reviewTotalCount) = " + reviewTotalCount);

       // ✅ PageMaker 설정
       PageMaker pageMaker = new PageMaker();
       pageMaker.setPagination(pagination);
       pageMaker.setReviewTotalCount(reviewTotalCount);

       // ✅ 페이징 적용된 리뷰 목록 가져오기
       List<RecipeReviewDTO> list = recipeReviewService.getAllRecipeReview(recipeBoardId, pagination);
       log.info("조회된 리뷰 개수 = " + list.size());

       // ✅ 결과 데이터 구성
       Map<String, Object> result = new HashMap<>();
       result.put("recipeReviews", list);
       result.put("pagination", pageMaker);
       result.put("currentUserId", currentUserId); // ✅ 현재 로그인한 사용자 ID 추가

       System.out.println("응답 데이터: " + result); // 🔥 콘솔에서 데이터 확인

       return new ResponseEntity<>(result, HttpStatus.OK);
   }
      
   @PreAuthorize("hasRole('ROLE_MEMBER')")
   @PutMapping("/reviews/{recipeReviewId}")
   public ResponseEntity<Integer> updateRecipeReview(
         @PathVariable("recipeReviewId") int recipeReviewId,
         @RequestBody RecipeReviewDTO recipeReviewDTO
         ){
      log.info("updateRecipeReview()");
      
      Integer currentUserId = getCurrentUserId();
      if (currentUserId == null || !currentUserId.equals(recipeReviewDTO.getMemberId())) {
          return new ResponseEntity<>(HttpStatus.FORBIDDEN); // 권한 없음
      }
      
      log.info("recipeReviewDTO = " + recipeReviewDTO);
      int result = recipeReviewService.updateRecipeReview(recipeReviewDTO);
      
      // 리뷰 수정 후 별점 평균 갱신
      recipeBoardService.updateAverageRating(recipeReviewDTO.getRecipeBoardId());

      return new ResponseEntity<Integer>(result, HttpStatus.OK);
   }
      
   @PreAuthorize("hasRole('ROLE_MEMBER')")
   @DeleteMapping("/reviews/{recipeReviewId}/{recipeBoardId}")
   public ResponseEntity<Integer> deleteRecipeReview(
         @PathVariable("recipeReviewId") int recipeReviewId,
         @PathVariable("recipeBoardId") int recipeBoardId) {
       // 🔥 현재 Security 인증 정보 확인
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      log.info("현재 인증 정보: " + authentication);
      log.info("현재 사용자: " + authentication.getPrincipal());
      
      // 현재 로그인한 사용자 ID 가져오기
      Integer currentUserId = getCurrentUserId();
      if (currentUserId == null) {
          return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // 로그인 안 된 경우
      }

      // 📌 삭제할 리뷰 정보 조회 (DB에서 가져옴)
      RecipeReviewDTO recipeReviewDTO = recipeReviewService.getReviewById(recipeReviewId);
      if (recipeReviewDTO == null) {
          return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 리뷰가 존재하지 않음
      }

      // 현재 사용자와 리뷰 작성자가 동일한지 확인
      if (!currentUserId.equals(recipeReviewDTO.getMemberId())) {
          return new ResponseEntity<>(HttpStatus.FORBIDDEN); // 권한 없음
      }
      
      int result = recipeReviewService.deleteRecipeReview(recipeReviewId, recipeBoardId);
      
      // 리뷰 삭제 후 별점 평균 갱신
      recipeBoardService.updateAverageRating(recipeBoardId);
      
      return new ResponseEntity<Integer>(result, HttpStatus.OK);
   }
   
   private Integer getCurrentUserId() {
       Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
       if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof CustomUser) {
           CustomUser customUser = (CustomUser) authentication.getPrincipal();
           return customUser.getMemberVO().getMemberId(); // CustomUser에서 memberId를 가져옴
       }
       return null; // 인증되지 않은 경우 null 반환
   }
   
}
