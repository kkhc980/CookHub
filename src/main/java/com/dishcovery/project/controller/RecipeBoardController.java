package com.dishcovery.project.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.dishcovery.project.domain.IngredientsVO;
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
            @RequestParam(value = "pageSize", defaultValue = "5") int pageSize,
            @RequestParam(value = "ingredientIds", required = false) String ingredientIdsStr,
            @RequestParam(value = "typeId", defaultValue = "1") Integer typeId,
            @RequestParam(value = "situationId", defaultValue = "1") Integer situationId,
            @RequestParam(value = "methodId", defaultValue = "1") Integer methodId,
            Model model) {

        // Pagination 설정
        Pagination pagination = new Pagination(pageNum, pageSize);
        pagination.setIngredientIdsFromString(ingredientIdsStr);
        pagination.setTypeId(typeId);
        pagination.setSituationId(situationId);
        pagination.setMethodId(methodId);

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
        addCommonAttributes(model);
        return "recipeboard/register";
    }

    @PostMapping("/register")
    public String registerRecipe(RecipeBoardVO recipeBoard,
                                 @RequestParam(value = "ingredientIds", required = false) List<Integer> ingredientIds,
                                 @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail) {
    	log.info("Ingredient IDs: " + ingredientIds);
        recipeBoardService.createRecipeWithIngredients(recipeBoard, ingredientIds, thumbnail);
        return "redirect:/recipeboard/list";
    }

    @GetMapping("/detail/{recipeBoardId}")
    public String getRecipeDetail(@PathVariable int recipeBoardId, Model model) {
        log.info("Fetching details for recipeBoardId: " + recipeBoardId);

        RecipeDetailVO detail = recipeBoardService.getRecipeDetailById(recipeBoardId);

        if (detail == null) {
            log.warn("RecipeBoard with ID " + recipeBoardId + " not found");
            return "redirect:/recipeboard/list";
        }

        model.addAttribute("recipeBoard", detail.getRecipeBoard());
        model.addAttribute("typeName", detail.getTypeName());
        model.addAttribute("methodName", detail.getMethodName());
        model.addAttribute("situationName", detail.getSituationName());
        model.addAttribute("ingredients", detail.getIngredients());

        return "recipeboard/detail";
    }
    
    @GetMapping("/update/{recipeBoardId}")
    public String updateForm(@PathVariable int recipeBoardId, Model model) {
        log.info("Navigating to the update page for recipeBoardId: " + recipeBoardId);

        RecipeBoardVO recipeBoard = recipeBoardService.getByRecipeBoardId(recipeBoardId);
        List<IngredientsVO> allIngredients = recipeBoardService.getAllIngredients();

        Set<Integer> selectedIngredientIds = recipeBoardService.getSelectedIngredientIdsByRecipeId(recipeBoardId);
        
        model.addAttribute("recipeBoard", recipeBoard);
        model.addAttribute("allIngredients", allIngredients);
        model.addAttribute("selectedIngredientIds", selectedIngredientIds); // 전달
        model.addAttribute("typesList", recipeBoardService.getAllTypes());
        model.addAttribute("methodsList", recipeBoardService.getAllMethods());
        model.addAttribute("situationsList", recipeBoardService.getAllSituations());

        return "recipeboard/update";
    }

    @PostMapping("/update")
    public String updateRecipe(RecipeBoardVO recipeBoard, @RequestParam("ingredientIds") List<Integer> ingredientIds) {
        log.info("Updating recipe: " + recipeBoard);

        // Update recipe and ingredients
        recipeBoardService.updateRecipeWithIngredients(recipeBoard, ingredientIds);

        return "redirect:/recipeboard/detail/" + recipeBoard.getRecipeBoardId();
    }
    
    @PostMapping("/delete/{recipeBoardId}")
    public String deleteRecipe(@PathVariable int recipeBoardId) {
        log.info("Deleting recipe with ID: " + recipeBoardId);

<<<<<<< HEAD
    // Helper method to add common attributes to the model
    private void addCommonAttributes(Model model) {
        model.addAttribute("typesList", recipeBoardService.getAllTypes());
        model.addAttribute("methodsList", recipeBoardService.getAllMethods());
        model.addAttribute("situationsList", recipeBoardService.getAllSituations());
        model.addAttribute("ingredientsList", recipeBoardService.getAllIngredients());
    }
    
    @GetMapping("/thumbnail/{recipeBoardId}")
    public ResponseEntity<Resource> getThumbnail(@PathVariable int recipeBoardId) {
        try {
            Resource resource = recipeBoardService.getThumbnailByRecipeBoardId(recipeBoardId);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(resource.getFile().toPath()))
                    .body(resource);
        } catch (FileNotFoundException e) {
            log.warn(e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            log.error("Error fetching thumbnail", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
=======
        // Delete recipe
        recipeBoardService.deleteRecipe(recipeBoardId);

        return "redirect:/recipeboard/list";
>>>>>>> 4f43097c6fcf0796edeff1ff812f892935944176
    }
}