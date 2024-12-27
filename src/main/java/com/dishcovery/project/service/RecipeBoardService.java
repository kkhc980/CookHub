package com.dishcovery.project.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.dishcovery.project.domain.IngredientsVO;
import com.dishcovery.project.domain.MethodsVO;
import com.dishcovery.project.domain.RecipeBoardVO;
import com.dishcovery.project.domain.RecipeDetailVO;
import com.dishcovery.project.domain.SituationsVO;
import com.dishcovery.project.domain.TypesVO;
import com.dishcovery.project.util.Pagination;

public interface RecipeBoardService {

    // Recipe CRUD
    RecipeBoardVO getByRecipeBoardId(int recipeBoardId); // 특정 Recipe 조회
    void createRecipeWithIngredients(RecipeBoardVO recipeBoard, List<Integer> ingredientIds, MultipartFile thumbnail); // Recipe 등록 및 재료 추가
    void updateRecipeWithIngredients(RecipeBoardVO recipeBoard, List<Integer> ingredientIds, MultipartFile thumbnail); // Recipe 업데이트 및 재료 변경
    void deleteRecipe(int recipeBoardId); // Recipe 삭제

    // Recipe Details
    RecipeDetailVO getRecipeDetailById(int recipeBoardId); // Recipe 상세 정보 조회

    // Ingredients Management
    List<IngredientsVO> getIngredientsByRecipeId(int recipeBoardId); // 특정 Recipe의 재료 조회
    List<IngredientsVO> getAllIngredients(); // 모든 재료 조회
    Set<Integer> getSelectedIngredientIdsByRecipeId(int recipeBoardId); // 특정 Recipe의 재료 ID 목록 조회

    // Types, Methods, Situations
    List<TypesVO> getAllTypes(); // 모든 Type 조회
    List<MethodsVO> getAllMethods(); // 모든 Method 조회
    List<SituationsVO> getAllSituations(); // 모든 Situation 조회

    // Pagination & Filtering
    Map<String, Object> getRecipeBoardListWithFilters(Pagination pagination); // 필터 및 페이징 적용 Recipe 목록 조회
    Pagination preprocessPagination(Pagination pagination); // 필터 데이터 전처리
    
    // thumbnail
    Resource getThumbnailByRecipeBoardId(int recipeBoardId);
}