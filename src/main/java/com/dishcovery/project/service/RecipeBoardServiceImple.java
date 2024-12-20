package com.dishcovery.project.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dishcovery.project.domain.IngredientsVO;
import com.dishcovery.project.domain.MethodsVO;
import com.dishcovery.project.domain.RecipeBoardVO;
import com.dishcovery.project.domain.RecipeIngredientsVO;
import com.dishcovery.project.domain.SituationsVO;
import com.dishcovery.project.domain.TypesVO;
import com.dishcovery.project.persistence.RecipeBoardMapper;

import lombok.extern.log4j.Log4j;

@Service
@Log4j
public class RecipeBoardServiceImple implements RecipeBoardService {

    private final RecipeBoardMapper mapper;

    public RecipeBoardServiceImple(RecipeBoardMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<RecipeBoardVO> getRecipeBoardList() {
        log.info("Fetching all recipe board entries");
        return mapper.getRecipeBoardList();
    }

    @Override
    public RecipeBoardVO getByRecipeBoardId(int recipeBoardId) {
        log.info("Fetching recipe board entry with ID: " + recipeBoardId);
        return mapper.getByRecipeBoardId(recipeBoardId);
    }

    @Override
    public void create(RecipeBoardVO recipeBoard) {
        log.info("Fetching next recipe board ID from sequence...");
        int nextId = mapper.getNextRecipeBoardId(); // SEQUENCE 값 가져오기
        recipeBoard.setRecipeBoardId(nextId);       // SEQUENCE 값을 VO에 설정
        log.info("Next RecipeBoard ID: " + nextId);

        log.info("Inserting recipe board: " + recipeBoard);
        mapper.insertRecipeBoard(recipeBoard);      // INSERT 쿼리 실행
    }
    
    @Override
    public void update(RecipeBoardVO recipeBoard) {
        log.info("Service: Updating RecipeBoard with ID: " + recipeBoard.getRecipeBoardId());
        mapper.update(recipeBoard);
    }

    @Override
    public void delete(int recipeBoardId) {
        log.info("Service: Deleting RecipeBoard with ID: " + recipeBoardId);
        mapper.delete(recipeBoardId);
    }

    // Ingredients-related methods
    @Override
    public List<IngredientsVO> getAllIngredients() {
        log.info("Fetching all ingredients");
        return mapper.getAllIngredients();
    }

    @Override
    public List<IngredientsVO> getIngredientsByRecipeId(int recipeBoardId) {
        log.info("Fetching ingredients for recipe ID: " + recipeBoardId);
        return mapper.getIngredientsByRecipeId(recipeBoardId);
    }

    @Override
    public void addIngredientsToRecipe(List<RecipeIngredientsVO> recipeIngredients) {
        log.info("Adding ingredients to recipe");
        for (RecipeIngredientsVO ingredient : recipeIngredients) {
            mapper.insertRecipeIngredient(ingredient);
        }
    }

    @Override
    public void removeIngredientsFromRecipe(int recipeBoardId) {
        log.info("Removing all ingredients from recipe with ID: " + recipeBoardId);
        mapper.removeIngredientsFromRecipe(recipeBoardId);
    }

    // Types, Methods, and Situations
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
    public String getTypeName(int typeId) {
        return mapper.getTypeName(typeId);
    }

    @Override
    public String getMethodName(int methodId) {
        return mapper.getMethodName(methodId);
    }

    @Override
    public String getSituationName(int situationId) {
        return mapper.getSituationName(situationId);
    }
    
    @Override
    public List<Integer> getIngredientIdListByRecipeId(int recipeBoardId) {
        log.info("Fetching ingredient IDs for RecipeBoard ID: " + recipeBoardId);
        return mapper.getIngredientIdListByRecipeId(recipeBoardId);
    }
    
    @Override
    @Transactional
    public void updateRecipeWithIngredients(RecipeBoardVO recipeBoard, List<Integer> ingredientIds) {
        log.info("Updating RecipeBoard and associated ingredients for ID: " + recipeBoard.getRecipeBoardId());

        // 1. Update RecipeBoard
        mapper.update(recipeBoard);

        // 2. Remove existing ingredients
        mapper.removeIngredientsFromRecipe(recipeBoard.getRecipeBoardId());
        log.info("Removed existing ingredients for RecipeBoard ID: " + recipeBoard.getRecipeBoardId());

        // 3. Add new ingredients
        if (ingredientIds != null && !ingredientIds.isEmpty()) {
            List<RecipeIngredientsVO> recipeIngredients = ingredientIds.stream()
                .map(ingredientId -> new RecipeIngredientsVO(recipeBoard.getRecipeBoardId(), ingredientId))
                .collect(Collectors.toList());
            mapper.addIngredientsToRecipe(recipeIngredients);
            log.info("Added new ingredients for RecipeBoard ID: " + recipeBoard.getRecipeBoardId());
        }
    }
    
    @Override
    public List<RecipeBoardVO> filterByCategory(Integer typeId, Integer situationId, Integer ingredientId, Integer methodId) {
        log.info("Service: Filtering recipes by category");
        return mapper.filterByCategory(typeId, situationId, ingredientId, methodId);
    }
}