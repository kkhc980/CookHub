package com.dishcovery.project.persistence;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.dishcovery.project.domain.IngredientsVO;
import com.dishcovery.project.domain.MethodsVO;
import com.dishcovery.project.domain.RecipeBoardVO;
import com.dishcovery.project.domain.RecipeIngredientsVO;
import com.dishcovery.project.domain.SituationsVO;
import com.dishcovery.project.domain.TypesVO;
import com.dishcovery.project.util.Pagination;

@Mapper
@Repository
public interface RecipeBoardMapper {

    RecipeBoardVO getByRecipeBoardId(int recipeBoardId);

    void updateRecipeBoard(RecipeBoardVO recipeBoard);

    void deleteRecipeBoard(int recipeBoardId);

    // Ingredients and RecipeIngredients methods
    List<IngredientsVO> getAllIngredients();

    List<IngredientsVO> getIngredientsByRecipeId(int recipeBoardId);

    void insertRecipeIngredient(RecipeIngredientsVO recipeIngredients);

    void deleteRecipeIngredientsByRecipeId(int recipeBoardId);

    List<TypesVO> getAllTypes();

    List<MethodsVO> getAllMethods();

    List<SituationsVO> getAllSituations();

    String getTypeName(int typeId);

    String getMethodName(int methodId);

    String getSituationName(int situationId);

    int getNextRecipeBoardId(); // SEQUENCE에서 다음 값을 가져오는 메서드.

    void insertRecipeBoard(RecipeBoardVO recipeBoard); // INSERT 메서드.
    
    List<RecipeBoardVO> getRecipeBoardListWithPaging(Pagination pagination);

    int getTotalCountWithFilters(Pagination pagination);
    
    int getTotalCount();
    
    List<RecipeBoardVO> getRecipeBoardListWithFilters(Map<String, Object> filters);
}