package com.dishcovery.project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dishcovery.project.domain.RecipeReviewDTO;
import com.dishcovery.project.service.RecipeReviewService;

import lombok.extern.log4j.Log4j;

@RestController
@RequestMapping(value = "/recipeboard")
@Log4j
public class RecipeReviewRESTController {
   
   @Autowired
   private RecipeReviewService recipeReviewService;
   
   @PreAuthorize("hasRole('ROLE_MEMBER')")
   @PostMapping("/reviews/detail")
   public ResponseEntity<Integer> createRecipeReview(@RequestBody RecipeReviewDTO recipeReviewDTO) {
      log.info("createRecipeReview()");
      log.info("recipReviewDTO = " + recipeReviewDTO);
      
      int result = recipeReviewService.createRecipeReview(recipeReviewDTO);
      log.info(result + "리뷰 등록");
      return new ResponseEntity<Integer>(result, HttpStatus.OK);
      
   }
   
   
   @GetMapping("/allReviews/{recipeBoardId}")
   public ResponseEntity<List<RecipeReviewDTO>> readAllRecipeReview(
         @PathVariable("recipeBoardId") int recipeBoardId) {
      log.info("readAllRecipeReview()");
      log.info("recipeBoardId = " + recipeBoardId);

      List<RecipeReviewDTO> list = recipeReviewService.getAllRecipeReview(recipeBoardId);

      return new ResponseEntity<List<RecipeReviewDTO>>(list, HttpStatus.OK);
   }
   
//   @PreAuthorize("principal.username == #recipeReviewDTO.memberId")
   
   @PreAuthorize("hasRole('ROLE_MEMBER')")
   @PutMapping("/reviews/{recipeReviewId}")
   public ResponseEntity<Integer> updateRecipeReview(
         @PathVariable("recipeReviewId") int recipeReviewId,
         @RequestBody RecipeReviewDTO recipeReviewDTO
         ){
      log.info("updateRecipeReview()");
      log.info("recipeReviewDTO = " + recipeReviewDTO);
      int result = recipeReviewService.updateRecipeReview(recipeReviewDTO);
      return new ResponseEntity<Integer>(result, HttpStatus.OK);
   }
   
//   @PreAuthorize("principal.username == #memberId")
   
   @PreAuthorize("hasRole('ROLE_MEMBER')")
   @DeleteMapping("/reviews/{recipeReviewId}/{recipeBoardId}")
   public ResponseEntity<Integer> deleteRecipeReview(
         @PathVariable("recipeReviewId") int recipeReviewId,
         @PathVariable("recipeBoardId") int recipeBoardId) {
      log.info("deleteRecipe()");
      log.info("recipeReviewId = " + recipeReviewId);

      int result = recipeReviewService.deleteRecipeReview(recipeReviewId, recipeBoardId);
      
      return new ResponseEntity<Integer>(result, HttpStatus.OK);
   }

}
