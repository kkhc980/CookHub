package com.dishcovery.project.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.dishcovery.project.domain.IngredientsVO;
import com.dishcovery.project.domain.MethodsVO;
import com.dishcovery.project.domain.RecipeBoardVO;
import com.dishcovery.project.domain.RecipeIngredientsVO;
import com.dishcovery.project.domain.SituationsVO;
import com.dishcovery.project.domain.TypesVO;
import com.dishcovery.project.service.RecipeBoardService;

import lombok.extern.log4j.Log4j;

@Controller
@RequestMapping("/recipeboard")
@Log4j
public class RecipeBoardController {

    private final RecipeBoardService recipeBoardService;

    public RecipeBoardController(RecipeBoardService recipeBoardService) {
        this.recipeBoardService = recipeBoardService;
    }

    @GetMapping("/list")
    public String list(Model model) {
        log.info("Fetching recipe board list");
        model.addAttribute("recipeList", recipeBoardService.getRecipeBoardList());
        model.addAttribute("typesList", recipeBoardService.getAllTypes());
        model.addAttribute("methodsList", recipeBoardService.getAllMethods());
        model.addAttribute("situationsList", recipeBoardService.getAllSituations());
        return "recipeboard/list";
    }
    
    @GetMapping("/register")
    public String register(Model model) {
        log.info("Navigating to the recipe registration page");
        List<TypesVO> typesList = recipeBoardService.getAllTypes();
        List<MethodsVO> methodsList = recipeBoardService.getAllMethods();
        List<SituationsVO> situationsList = recipeBoardService.getAllSituations();
        List<IngredientsVO> ingredientsList = recipeBoardService.getAllIngredients();
        
        log.info("Types: " + typesList);
        log.info("Methods: " + methodsList);
        log.info("Situations: " + situationsList);
        log.info("Ingredients: " + ingredientsList);

        model.addAttribute("typesList", typesList);
        model.addAttribute("methodsList", methodsList);
        model.addAttribute("situationsList", situationsList);
        model.addAttribute("ingredientsList", ingredientsList);
        return "recipeboard/register";
    }
    
    @PostMapping("/register")
    public String registerRecipe(RecipeBoardVO recipeBoard, @RequestParam("ingredientIds") List<Integer> ingredientIds) {
        log.info("Registering a new recipe: " + recipeBoard);
        log.info("Selected ingredients: " + ingredientIds);

        // Save recipe to RecipeBoard table
        recipeBoardService.create(recipeBoard);

        // Retrieve the auto-generated recipeBoardId
        int recipeBoardId = recipeBoard.getRecipeBoardId();
        log.info("New Recipe ID: " + recipeBoardId);

        // Save selected ingredients to RecipeIngredients table
        if (ingredientIds != null && !ingredientIds.isEmpty()) {
            List<RecipeIngredientsVO> recipeIngredients = ingredientIds.stream()
                .map(ingredientId -> {
                    RecipeIngredientsVO recipeIngredient = new RecipeIngredientsVO();
                    recipeIngredient.setRecipeBoardId(recipeBoardId);
                    recipeIngredient.setIngredientId(ingredientId);
                    return recipeIngredient;
                })
                .collect(Collectors.toList());
            recipeBoardService.addIngredientsToRecipe(recipeIngredients);
        }

        return "redirect:/recipeboard/list";
    }
    
    @GetMapping("/detail/{recipeBoardId}")
    public String getRecipeDetail(@PathVariable int recipeBoardId, Model model) {
        log.info("Fetching details for recipeBoardId: " + recipeBoardId);

        // Fetch recipe board details
        RecipeBoardVO recipeBoard = recipeBoardService.getByRecipeBoardId(recipeBoardId);
        if (recipeBoard == null) {
            log.warn("RecipeBoard with ID " + recipeBoardId + " not found");
            return "redirect:/recipeboard/list";
        }

        // Fetch names for type, method, and situation
        String typeName = recipeBoardService.getTypeName(recipeBoard.getTypeId());
        String methodName = recipeBoardService.getMethodName(recipeBoard.getMethodId());
        String situationName = recipeBoardService.getSituationName(recipeBoard.getSituationId());

        // Fetch ingredients
        List<IngredientsVO> ingredients = recipeBoardService.getIngredientsByRecipeId(recipeBoardId);

        // Add data to the model
        model.addAttribute("recipeBoard", recipeBoard);
        model.addAttribute("typeName", typeName);
        model.addAttribute("methodName", methodName);
        model.addAttribute("situationName", situationName);
        model.addAttribute("ingredients", ingredients);

        return "recipeboard/detail";
    }

    @GetMapping("/update/{recipeBoardId}")
    public String updateRecipePage(@PathVariable int recipeBoardId, Model model) {
        log.info("Loading update page for RecipeBoard ID: " + recipeBoardId);

        RecipeBoardVO recipeBoard = recipeBoardService.getByRecipeBoardId(recipeBoardId);
        if (recipeBoard == null) {
            log.warn("RecipeBoard with ID " + recipeBoardId + " not found");
            return "redirect:/recipeboard/list";
        }

        // Fetch related data
        List<TypesVO> typesList = recipeBoardService.getAllTypes();
        List<MethodsVO> methodsList = recipeBoardService.getAllMethods();
        List<SituationsVO> situationsList = recipeBoardService.getAllSituations();
        List<IngredientsVO> ingredientsList = recipeBoardService.getAllIngredients();
        List<Integer> recipeIngredientIds = recipeBoardService.getIngredientIdListByRecipeId(recipeBoardId);

        model.addAttribute("recipeBoard", recipeBoard);
        model.addAttribute("typesList", typesList);
        model.addAttribute("methodsList", methodsList);
        model.addAttribute("situationsList", situationsList);
        model.addAttribute("ingredientsList", ingredientsList);
        model.addAttribute("recipeIngredientIds", recipeIngredientIds); // List<Integer>로 전달

        return "recipeboard/update";
    }

    @PostMapping("/update/{recipeBoardId}")
    public String updateRecipe(@PathVariable int recipeBoardId, RecipeBoardVO recipeBoard,
                               @RequestParam(value = "ingredientIds", required = false) List<Integer> ingredientIds) {
        log.info("Processing update for RecipeBoard ID: " + recipeBoardId);

        recipeBoard.setRecipeBoardId(recipeBoardId);
        recipeBoardService.updateRecipeWithIngredients(recipeBoard, ingredientIds);

        log.info("Successfully updated RecipeBoard ID: " + recipeBoardId);
        return "redirect:/recipeboard/detail/" + recipeBoardId;
    }

    
    @PostMapping("/delete/{recipeBoardId}")
    public String deleteRecipe(@PathVariable int recipeBoardId) {
        log.info("Deleting RecipeBoard with ID: " + recipeBoardId);

        recipeBoardService.delete(recipeBoardId);

        log.info("Successfully deleted RecipeBoard with ID: " + recipeBoardId);
        return "redirect:/recipeboard/list"; // 삭제 후 목록으로 이동
    }
    
    @GetMapping("/filter")
    public String filterByConditions(
            @RequestParam(value = "typeId", required = false, defaultValue = "1") int typeId,
            @RequestParam(value = "situationId", required = false, defaultValue = "1") int situationId,
            @RequestParam(value = "methodId", required = false, defaultValue = "1") int methodId,
            @RequestParam(value = "ingredientIds", required = false) List<Integer> ingredientIds, // 다중 선택
            Model model) {

        log.info("Filtering RecipeBoards by Type ID: {}, Situation ID: {}, Method ID: {}, Ingredient IDs: {}" +
                 typeId + situationId + methodId + ingredientIds);

        // 필터링된 레시피 가져오기
        List<RecipeBoardVO> filteredList = recipeBoardService.getFilteredRecipeBoards(typeId, situationId, methodId, ingredientIds);

        // 필터 데이터 추가
        model.addAttribute("recipeList", filteredList);
        model.addAttribute("typesList", recipeBoardService.getAllTypes());
        model.addAttribute("situationsList", recipeBoardService.getAllSituations());
        model.addAttribute("methodsList", recipeBoardService.getAllMethods());
        model.addAttribute("ingredientsList", recipeBoardService.getAllIngredients());
        model.addAttribute("selectedTypeId", typeId);
        model.addAttribute("selectedSituationId", situationId);
        model.addAttribute("selectedMethodId", methodId);
        model.addAttribute("selectedIngredientIds", ingredientIds);

        return "recipeboard/list";
    }
}