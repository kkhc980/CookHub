package com.dishcovery.project.service;

import java.util.List;

import com.dishcovery.project.domain.IngredientsVO;
import com.dishcovery.project.domain.MethodsVO;
import com.dishcovery.project.domain.RecipeBoardVO;
import com.dishcovery.project.domain.RecipeIngredientsVO;
import com.dishcovery.project.domain.SituationsVO;
import com.dishcovery.project.domain.TypesVO;

public interface RecipeBoardService {

    List<RecipeBoardVO> getRecipeBoardList();

    RecipeBoardVO getByRecipeBoardId(int recipeBoardId);

    void create(RecipeBoardVO recipeBoard);

    void update(RecipeBoardVO recipeBoard);

    void delete(int recipeBoardId);
    
    List<IngredientsVO> getAllIngredients();

    List<IngredientsVO> getIngredientsByRecipeId(int recipeBoardId);

    void addIngredientsToRecipe(List<RecipeIngredientsVO> recipeIngredients);

    void removeIngredientsFromRecipe(int recipeBoardId);
    
    List<TypesVO> getAllTypes();

    List<MethodsVO> getAllMethods();

    List<SituationsVO> getAllSituations();
    
    String getTypeName(int typeId);
    
    String getMethodName(int methodId);
    
    String getSituationName(int situationId);
    
    List<Integer> getIngredientIdListByRecipeId(int recipeBoardId);
    
    void updateRecipeWithIngredients(RecipeBoardVO recipeBoard, List<Integer> ingredientIds);
    
    List<RecipeBoardVO> getFilteredRecipeBoards(int typeId, int situationId, int methodId, List<Integer> ingredientIds);
}