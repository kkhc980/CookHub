package com.dishcovery.project.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dishcovery.project.domain.IngredientsVO;
import com.dishcovery.project.domain.MethodsVO;
import com.dishcovery.project.domain.RecipeBoardVO;
import com.dishcovery.project.domain.RecipeDetailVO;
import com.dishcovery.project.domain.SituationsVO;
import com.dishcovery.project.domain.TypesVO;
import com.dishcovery.project.util.Pagination;

public interface RecipeBoardService {

    RecipeBoardVO getByRecipeBoardId(int recipeBoardId);

    void createRecipeWithIngredients(RecipeBoardVO recipeBoard, List<Integer> ingredientIds);

    RecipeDetailVO getRecipeDetailById(int recipeBoardId); // 추가: Recipe 상세 정보 반환

    List<IngredientsVO> getIngredientsByRecipeId(int recipeBoardId);
    
    List<TypesVO> getAllTypes();

    List<MethodsVO> getAllMethods();

    List<SituationsVO> getAllSituations();

    List<IngredientsVO> getAllIngredients();
    
    void updateRecipeWithIngredients(RecipeBoardVO recipeBoard, List<Integer> ingredientIds);
    
    void deleteRecipe(int recipeBoardId);

    Set<Integer> getSelectedIngredientIdsByRecipeId(int recipeBoardId);

    Map<String, Object> getRecipeBoardListWithFilters(Pagination pagination);

}