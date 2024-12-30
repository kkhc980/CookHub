package com.dishcovery.project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dishcovery.project.domain.RecipeReviewVO;
import com.dishcovery.project.service.RecipeReviewService;

import lombok.extern.log4j.Log4j;

@RestController
@RequestMapping(value = "/recipeboard")
@Log4j
public class RecipeReviewRESTController {
	@Autowired
	private RecipeReviewService recipeReviewService;

	@PostMapping("reviews/detail")
	public ResponseEntity<Integer> createRecipeReview(@RequestBody RecipeReviewVO recipeReviewVO) {
		log.info("createRecipeReview()");

		int result = recipeReviewService.createRecipeReview(recipeReviewVO);
		return new ResponseEntity<Integer>(result, HttpStatus.OK);
	}

	@GetMapping("/allReviews/{recipeBoardId}")
	public ResponseEntity<List<RecipeReviewVO>> readAllRecipeReview(@PathVariable("recipeBoardId") int recipeBoardId) {
		log.info("readAllRecipeReview()");
		log.info("recipeBoardId = " + recipeBoardId);

		List<RecipeReviewVO> list = recipeReviewService.getAllRecipeReview(recipeBoardId);

		return new ResponseEntity<List<RecipeReviewVO>>(list, HttpStatus.OK);
	}

	@PutMapping("reviews/{recipeReviewId}")
	public ResponseEntity<Integer> updateRecipeReview(@PathVariable("recipeReviewId") int recipeReviewId,
			@RequestBody String recipeContent) {
		log.info("updateRecipeReview()");
		log.info("recipeReviewId = " + recipeReviewId);
		int result = recipeReviewService.updateRecipeReview(recipeReviewId, recipeContent);
		return new ResponseEntity<Integer>(result, HttpStatus.OK);
	}

	@DeleteMapping("reviews/{recipeReviewId}/{recipeBoardId}")
	public ResponseEntity<Integer> deleteRecipeReview(@PathVariable("recipeReviewId") int recipeReviewId,
		@PathVariable("recipeBoardId") int recipeBoardId) {
	log.info("deleteRecipe()");
		log.info("recipeReviewId = " + recipeReviewId);

		int result = recipeReviewService.deleteRecipeReview(recipeReviewId, recipeBoardId);
		return new ResponseEntity<Integer>(result, HttpStatus.OK);
	}

}
