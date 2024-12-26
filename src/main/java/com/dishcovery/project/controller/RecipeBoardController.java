package com.dishcovery.project.controller;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    public String list(@RequestParam(value = "pageNum", required = false, defaultValue = "1") int pageNum,
                       @RequestParam(value = "pageSize", required = false, defaultValue = "5") int pageSize,
                       @RequestParam(value = "ingredientIds", required = false) List<Integer> ingredientIds,
                       @RequestParam(value = "typeId", required = false) Integer typeId,
                       @RequestParam(value = "situationId", required = false) Integer situationId,
                       @RequestParam(value = "methodId", required = false) Integer methodId,
                       Model model) {
        // Pagination 객체 생성 및 필터 조건 설정
    	Pagination pagination = new Pagination(pageNum, pageSize);

        // 각 카테고리의 전체(id = 1) 선택 시 필터에서 제외
    	pagination.setIngredientIds(ingredientIds != null && !ingredientIds.isEmpty() && !ingredientIds.contains(1) ? ingredientIds : null);
    	pagination.setTypeId(typeId != null && typeId == 1 ? null : typeId);
    	pagination.setSituationId(situationId != null && situationId == 1 ? null : situationId);
    	pagination.setMethodId(methodId != null && methodId == 1 ? null : methodId);

        // Service 호출
        Map<String, Object> result = recipeBoardService.getRecipeBoardListWithFilters(pagination);

        // Model에 데이터 추가
        model.addAttribute("recipeList", result.get("recipeList"));
        model.addAttribute("pageMaker", result.get("pageMaker"));
        model.addAttribute("allIngredients", result.get("allIngredients"));
        model.addAttribute("allTypes", result.get("allTypes"));
        model.addAttribute("allSituations", result.get("allSituations"));
        model.addAttribute("allMethods", result.get("allMethods"));

        return "recipeboard/list";
    }

    @GetMapping("/register")
    public String register(Model model) {
        log.info("Navigating to the recipe registration page");
        model.addAttribute("typesList", recipeBoardService.getAllTypes());
        model.addAttribute("methodsList", recipeBoardService.getAllMethods());
        model.addAttribute("situationsList", recipeBoardService.getAllSituations());
        model.addAttribute("ingredientsList", recipeBoardService.getAllIngredients());
        return "recipeboard/register";
    }

    @PostMapping("/register")
    public String registerRecipe(RecipeBoardVO recipeBoard, @RequestParam("ingredientIds") List<Integer> ingredientIds) {
        log.info("Registering a new recipe");
        recipeBoardService.createRecipeWithIngredients(recipeBoard, ingredientIds);
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

        // Delete recipe
        recipeBoardService.deleteRecipe(recipeBoardId);

        return "redirect:/recipeboard/list";
    }
}