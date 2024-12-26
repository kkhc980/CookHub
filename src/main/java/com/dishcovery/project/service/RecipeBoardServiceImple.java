package com.dishcovery.project.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dishcovery.project.domain.IngredientsVO;
import com.dishcovery.project.domain.MethodsVO;
import com.dishcovery.project.domain.RecipeBoardVO;
import com.dishcovery.project.domain.RecipeDetailVO;
import com.dishcovery.project.domain.RecipeIngredientsVO;
import com.dishcovery.project.domain.SituationsVO;
import com.dishcovery.project.domain.TypesVO;
import com.dishcovery.project.persistence.RecipeBoardMapper;
import com.dishcovery.project.util.PageMaker;
import com.dishcovery.project.util.Pagination;

import lombok.extern.log4j.Log4j;

@Service
@Log4j
public class RecipeBoardServiceImple implements RecipeBoardService {

    @Autowired
    RecipeBoardMapper mapper;

    @Override
    public RecipeBoardVO getByRecipeBoardId(int recipeBoardId) {
        log.info("Fetching recipe board entry with ID: " + recipeBoardId);
        return mapper.getByRecipeBoardId(recipeBoardId);
    }

    @Override
    @Transactional // 트랜잭션 적용
    public void createRecipeWithIngredients(RecipeBoardVO recipeBoard, List<Integer> ingredientIds) {
        log.info("Registering a new recipe with ingredients");

        try {
            // Step 1: Insert recipe board
            int nextId = mapper.getNextRecipeBoardId();
            recipeBoard.setRecipeBoardId(nextId);
            mapper.insertRecipeBoard(recipeBoard);

            // Step 2: Insert ingredients
            if (ingredientIds != null && !ingredientIds.isEmpty()) {
                List<RecipeIngredientsVO> recipeIngredients = ingredientIds.stream()
                    .map(ingredientId -> {
                        RecipeIngredientsVO recipeIngredient = new RecipeIngredientsVO();
                        recipeIngredient.setRecipeBoardId(nextId);
                        recipeIngredient.setIngredientId(ingredientId);
                        return recipeIngredient;
                    })
                    .collect(Collectors.toList());
                for (RecipeIngredientsVO ingredient : recipeIngredients) {
                    mapper.insertRecipeIngredient(ingredient);
                }
            }
        } catch (Exception e) {
            log.error("Error while creating recipe with ingredients: ", e);
            throw new RuntimeException("Transaction failed, rolling back."); // 트랜잭션 롤백 유도
        }
    }

    @Override
    public RecipeDetailVO getRecipeDetailById(int recipeBoardId) {
        log.info("Fetching recipe detail for ID: " + recipeBoardId);

        // Fetch recipe board details
        RecipeBoardVO recipeBoard = mapper.getByRecipeBoardId(recipeBoardId);
        if (recipeBoard == null) return null;

        // Fetch associated data
        String typeName = mapper.getTypeName(recipeBoard.getTypeId());
        String methodName = mapper.getMethodName(recipeBoard.getMethodId());
        String situationName = mapper.getSituationName(recipeBoard.getSituationId());
        List<IngredientsVO> ingredients = mapper.getIngredientsByRecipeId(recipeBoardId);

        // Assemble detail object
        RecipeDetailVO detail = new RecipeDetailVO();
        detail.setRecipeBoard(recipeBoard);
        detail.setTypeName(typeName);
        detail.setMethodName(methodName);
        detail.setSituationName(situationName);
        detail.setIngredients(ingredients);

        return detail;
    }
    
    @Override
    public List<IngredientsVO> getIngredientsByRecipeId(int recipeBoardId) {
        log.info("Fetching ingredients for recipe ID: " + recipeBoardId);
        return mapper.getIngredientsByRecipeId(recipeBoardId);
    }
    
    @Override
    public List<TypesVO> getAllTypes() {
        log.info("Fetching all types");
        return mapper.getAllTypes();
    }

    @Override
    public List<MethodsVO> getAllMethods() {
        log.info("Fetching all methods");
        return mapper.getAllMethods();
    }

    @Override
    public List<SituationsVO> getAllSituations() {
        log.info("Fetching all situations");
        return mapper.getAllSituations();
    }

    @Override
    public List<IngredientsVO> getAllIngredients() {
        log.info("Fetching all ingredients");
        return mapper.getAllIngredients();
    }
    
    @Override
    @Transactional
    public void updateRecipeWithIngredients(RecipeBoardVO recipeBoard, List<Integer> ingredientIds) {
        log.info("Updating recipe with ID: " + recipeBoard.getRecipeBoardId());

        try {
            // Step 1: Update recipe board
            mapper.updateRecipeBoard(recipeBoard);

            // Step 2: Remove existing ingredients
            mapper.deleteRecipeIngredientsByRecipeId(recipeBoard.getRecipeBoardId());

            // Step 3: Add new ingredients
            if (ingredientIds != null && !ingredientIds.isEmpty()) {
                List<RecipeIngredientsVO> recipeIngredients = ingredientIds.stream()
                    .map(ingredientId -> {
                        RecipeIngredientsVO recipeIngredient = new RecipeIngredientsVO();
                        recipeIngredient.setRecipeBoardId(recipeBoard.getRecipeBoardId());
                        recipeIngredient.setIngredientId(ingredientId);
                        return recipeIngredient;
                    })
                    .collect(Collectors.toList());
                for (RecipeIngredientsVO ingredient : recipeIngredients) {
                    mapper.insertRecipeIngredient(ingredient);
                }
            }
        } catch (Exception e) {
            log.error("Error while updating recipe: ", e);
            throw new RuntimeException("Transaction failed, rolling back.");
        }
    }
    
    @Override
    @Transactional
    public void deleteRecipe(int recipeBoardId) {
        log.info("Deleting recipe with ID: " + recipeBoardId);

        try {
            // Step 1: Delete related ingredients
            mapper.deleteRecipeIngredientsByRecipeId(recipeBoardId);

            // Step 2: Delete recipe board
            mapper.deleteRecipeBoard(recipeBoardId);
        } catch (Exception e) {
            log.error("Error while deleting recipe: ", e);
            throw new RuntimeException("Transaction failed, rolling back.");
        }
    }
    
    @Override
    public Set<Integer> getSelectedIngredientIdsByRecipeId(int recipeBoardId) {
        log.info("Fetching selected ingredient IDs for recipe ID: " + recipeBoardId);
        List<IngredientsVO> selectedIngredients = mapper.getIngredientsByRecipeId(recipeBoardId);
        return selectedIngredients.stream()
            .map(IngredientsVO::getIngredientId)
            .collect(Collectors.toSet());
    }
    
    @Override
    public Map<String, Object> getRecipeBoardListWithFilters(Pagination pagination) {
        Map<String, Object> result = new HashMap<>();

        // 전체 데이터 조회
        result.put("allIngredients", mapper.getAllIngredients());
        result.put("allTypes", mapper.getAllTypes());
        result.put("allSituations", mapper.getAllSituations());
        result.put("allMethods", mapper.getAllMethods());

        // 필터링 및 페이징 데이터 조회
        List<RecipeBoardVO> recipeList = mapper.getRecipeBoardListWithPaging(pagination);
        int totalCount = mapper.getTotalCountWithFilters(pagination);

        // PageMaker 생성
        PageMaker pageMaker = new PageMaker();
        pageMaker.setPagination(pagination);
        pageMaker.setTotalCount(totalCount);

        result.put("recipeList", recipeList);
        result.put("pageMaker", pageMaker);

        return result;
    }
}