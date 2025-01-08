package com.dishcovery.project.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.dishcovery.project.domain.HashtagsVO;
import com.dishcovery.project.domain.RecipeBoardVO;
import com.dishcovery.project.domain.RecipeDetailVO;
import com.dishcovery.project.service.RecipeBoardService;
import com.dishcovery.project.util.Pagination;

import lombok.extern.log4j.Log4j;

@Controller
@RequestMapping("/recipeboard")
@Log4j
public class RecipeBoardController {

    @Autowired
    RecipeBoardService recipeBoardService;

    @GetMapping("/list")
    public String list(
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "4") int pageSize,
            @RequestParam(value = "ingredientIds", required = false) String ingredientIdsStr,
            @RequestParam(value = "typeId", defaultValue = "1") Integer typeId,
            @RequestParam(value = "situationId", defaultValue = "1") Integer situationId,
            @RequestParam(value = "methodId", defaultValue = "1") Integer methodId,
            Model model) {

        // Pagination 설정
        Pagination pagination = new Pagination(pageNum, pageSize);
        pagination.setIngredientIdsFromString(ingredientIdsStr);
        pagination.setTypeId(typeId != null ? typeId : 1);
        pagination.setSituationId(situationId != null ? situationId : 1);
        pagination.setMethodId(methodId != null ? methodId : 1);
        
        // RecipeBoard 목록 및 관련 데이터 가져오기
        Map<String, Object> result = recipeBoardService.getRecipeBoardListWithFilters(
                recipeBoardService.preprocessPagination(pagination));

        // 모델에 데이터 추가
        model.addAllAttributes(result);
        model.addAttribute("selectedTypeId", typeId);
        model.addAttribute("selectedSituationId", situationId);
        model.addAttribute("selectedMethodId", methodId);
        model.addAttribute("ingredientIdsStr", pagination.getIngredientIdsAsString());
        model.addAttribute("selectedPageNum", pageNum);
        model.addAttribute("selectedIngredientIds", ingredientIdsStr != null ? Arrays.asList(ingredientIdsStr.split(",")) : List.of("1"));

        // 공통 레이아웃에 포함될 페이지 설정
        model.addAttribute("pageContent", "recipeboard/list.jsp");

        // 공통 레이아웃 반환
        return "layout";
    }


    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("typesList", recipeBoardService.getAllTypes());
        model.addAttribute("methodsList", recipeBoardService.getAllMethods());
        model.addAttribute("situationsList", recipeBoardService.getAllSituations());
        model.addAttribute("ingredientsList", recipeBoardService.getAllIngredients());
        return "recipeboard/register";
    }

    @PostMapping("/register")
    public String registerRecipe(
            RecipeBoardVO recipeBoard,
            @RequestParam(value = "ingredientIds", required = false) List<Integer> ingredientIds,
            @RequestParam(value = "hashtags", required = false) String hashtags,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail) {
    	log.info("Received hashtags: " + hashtags);
    	recipeBoardService.createRecipe(recipeBoard, ingredientIds, hashtags, thumbnail);
        return "redirect:/recipeboard/list";
    }

    @GetMapping("/detail/{recipeBoardId}")
    public String getRecipeDetail(@PathVariable int recipeBoardId, Model model) {
        RecipeDetailVO detail = recipeBoardService.getRecipeDetailById(recipeBoardId);

        if (detail == null) {
            return "redirect:/recipeboard/list";
        }

        model.addAttribute("recipeBoard", detail.getRecipeBoard());
        model.addAttribute("typeName", detail.getTypeName());
        model.addAttribute("methodName", detail.getMethodName());
        model.addAttribute("situationName", detail.getSituationName());
        model.addAttribute("ingredients", detail.getIngredients());
        model.addAttribute("hashtags", detail.getHashtags());
        return "recipeboard/detail";
    }

    @GetMapping("/update/{recipeBoardId}")
    public String updateForm(@PathVariable int recipeBoardId, Model model) {
        RecipeBoardVO recipeBoard = recipeBoardService.getByRecipeBoardId(recipeBoardId);
        if (recipeBoard == null) {
            return "redirect:/recipeboard/list";
        }

        model.addAttribute("recipeBoard", recipeBoard);
        model.addAttribute("selectedIngredientIds", recipeBoardService.getSelectedIngredientIdsByRecipeBoardId(recipeBoardId));
        model.addAttribute("typesList", recipeBoardService.getAllTypes());
        model.addAttribute("methodsList", recipeBoardService.getAllMethods());
        model.addAttribute("situationsList", recipeBoardService.getAllSituations());
        model.addAttribute("ingredientsList", recipeBoardService.getAllIngredients());
        model.addAttribute("hashtags", recipeBoardService.getHashtagsByRecipeBoardId(recipeBoardId));

        return "recipeboard/update";
    }

    @PostMapping("/update")
    public String updateRecipe(RecipeBoardVO recipeBoard,
                               @RequestParam(value = "ingredientIds", required = false) List<Integer> ingredientIds,
                               @RequestParam(value = "hashtags", required = false) String hashtags,
                               @RequestPart(value = "thumbnail", required = true) MultipartFile thumbnail) {
        try {
        	recipeBoardService.updateRecipe(recipeBoard, ingredientIds, hashtags, thumbnail);
            return "redirect:/recipeboard/detail/" + recipeBoard.getRecipeBoardId();
        } catch (IllegalArgumentException e) {
            log.error("Error updating recipe: " + e.getMessage());
            return "redirect:/recipeboard/update/" + recipeBoard.getRecipeBoardId() + "?error=" + e.getMessage();
        }
    }

    @PostMapping("/delete/{recipeBoardId}")
    public String deleteRecipe(@PathVariable int recipeBoardId) {
        recipeBoardService.deleteRecipe(recipeBoardId);
        return "redirect:/recipeboard/list";
    }

    @GetMapping("/thumbnail/{recipeBoardId}")
    public ResponseEntity<?> getThumbnail(@PathVariable int recipeBoardId) {
        return recipeBoardService.getThumbnailByRecipeBoardId(recipeBoardId)
                .map(resource -> ResponseEntity.ok(resource))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
