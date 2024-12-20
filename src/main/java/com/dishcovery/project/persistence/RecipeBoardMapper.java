package com.dishcovery.project.persistence;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.dishcovery.project.domain.IngredientsVO;
import com.dishcovery.project.domain.MethodsVO;
import com.dishcovery.project.domain.RecipeBoardVO;
import com.dishcovery.project.domain.RecipeIngredientsVO;
import com.dishcovery.project.domain.SituationsVO;
import com.dishcovery.project.domain.TypesVO;

@Mapper
@Repository
public interface RecipeBoardMapper {

    List<RecipeBoardVO> getRecipeBoardList();

    RecipeBoardVO getByRecipeBoardId(int recipeBoardId);

    void insert(RecipeBoardVO recipeBoard);

    void update(RecipeBoardVO recipeBoard);

    void delete(int recipeBoardId);

    // Ingredients and RecipeIngredients methods
    List<IngredientsVO> getAllIngredients();

    List<IngredientsVO> getIngredientsByRecipeId(int recipeBoardId);

    void insertRecipeIngredient(RecipeIngredientsVO recipeIngredients);
    
    List<TypesVO> getAllTypes();

    List<MethodsVO> getAllMethods();

    List<SituationsVO> getAllSituations();
    
    String getTypeName(int typeId);
    
    String getMethodName(int methodId);
    
    String getSituationName(int situationId);
    
    int getNextRecipeBoardId(); // SEQUENCE에서 다음 값을 가져오는 메서드
    
    void insertRecipeBoard(RecipeBoardVO recipeBoard); // INSERT 메서드

    List<Integer> getIngredientIdListByRecipeId(int recipeBoardId);
    
    void removeIngredientsFromRecipe(int recipeBoardId);
    
    void addIngredientsToRecipe(List<RecipeIngredientsVO> recipeIngredients);
    
    List<RecipeBoardVO> filterByCategory(@Param("typeId") Integer typeId,
            @Param("situationId") Integer situationId,
            @Param("ingredientId") Integer ingredientId,
            @Param("methodId") Integer methodId);
}