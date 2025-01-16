package com.dishcovery.project.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.dishcovery.project.domain.HashtagsVO;
import com.dishcovery.project.domain.IngredientsVO;
import com.dishcovery.project.domain.MethodsVO;
import com.dishcovery.project.domain.RecipeBoardVO;
import com.dishcovery.project.domain.RecipeDetailVO;
import com.dishcovery.project.domain.SituationsVO;
import com.dishcovery.project.domain.TypesVO;
import com.dishcovery.project.util.Pagination;

public interface RecipeBoardService {

    // Recipe CRUD
    RecipeBoardVO getByRecipeBoardId(int recipeBoardId);
    void createRecipe(RecipeBoardVO recipeBoard, List<Integer> ingredientIds, String hashtags, MultipartFile thumbnail);
    void updateRecipe(RecipeBoardVO recipeBoard, List<Integer> ingredientIds, String hashtags, MultipartFile thumbnail);
    void deleteRecipe(int recipeBoardId);

    // Recipe Details
    RecipeDetailVO getRecipeDetailById(int recipeBoardId);

    // Ingredients Management
    List<IngredientsVO> getIngredientsByRecipeId(int recipeBoardId);
    List<IngredientsVO> getAllIngredients();
    Set<Integer> getSelectedIngredientIdsByRecipeBoardId(int recipeBoardId);

    // Hashtags Management
    List<HashtagsVO> getHashtagsByRecipeBoardId(int recipeBoardId); // 특정 레시피의 해시태그 조회
    void saveHashtagsForRecipe(int recipeBoardId, String hashtags); // 해시태그 저장
    List<String> getHashtagNamesByRecipeBoardId(int recipeBoardId);
    void addHashtagsToRecipe(int recipeBoardId, List<String> hashtagsToAdd);
    void removeHashtagsFromRecipe(int recipeBoardId, List<String> hashtagsToRemove);
    
    // Types, Methods, Situations
    List<TypesVO> getAllTypes();
    List<MethodsVO> getAllMethods();
    List<SituationsVO> getAllSituations();

    // Pagination & Filtering
    Map<String, Object> getRecipeBoardListWithFilters(Pagination pagination);
    Pagination preprocessPagination(Pagination pagination);

    // Thumbnail Management
    Optional<Resource> getThumbnailByRecipeBoardId(int recipeBoardId); // Optional로 리소스 반환
    
    /**
     * 게시글 조회수를 증가시킵니다.
     * 동일한 IP는 하루에 한 번만 조회수를 증가시킵니다.
     *
     * @param recipeBoardId 게시글 ID
     * @param ipAddress     클라이언트 IP 주소
     */
    void increaseViewCountIfEligible(int recipeBoardId, String ipAddress);
}
