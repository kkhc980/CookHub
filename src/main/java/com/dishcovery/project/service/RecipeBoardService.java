package com.dishcovery.project.service;

import com.dishcovery.project.domain.*;
import com.dishcovery.project.util.Pagination;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface RecipeBoardService {

    // Recipe CRUD
    RecipeBoardVO getByRecipeBoardId(int recipeBoardId);

    void createRecipe(RecipeRegisterRequest request);

    void updateRecipe( RecipeUpdateRequest request);

    void deleteRecipe(int recipeBoardId);

    // Recipe Details
    RecipeBoardDTO getRecipeDetailById(int recipeBoardId);

    // Ingredients Management
    List<IngredientsVO> getIngredientsByRecipeId(int recipeBoardId);

    List<IngredientsVO> getAllIngredients();

    Set<Integer> getSelectedIngredientIdsByRecipeBoardId(int recipeBoardId);

    // Recipe Ingredient Details Management
    List<RecipeIngredientsDetailVO> getRecipeIngredientsDetailsByRecipeId(int recipeBoardId);

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


    default Pagination preprocessPagination(Pagination pagination) {
        if (pagination.getSort() == null || pagination.getSort().isEmpty()) {
            pagination.setSort("latest"); // 기본 정렬: 최신순
        }
        return pagination;
    }

    // Thumbnail Management
    Optional<ImageData> getThumbnailByRecipeBoardId(int recipeBoardId); // Optional로 리소스 반환

    /**
     * 게시글 조회수를 증가시킵니다. 동일한 IP는 하루에 한 번만 조회수를 증가시킵니다.
     *
     * @param recipeBoardId 게시글 ID
     * @param ipAddress     클라이언트 IP 주소
     */
    void increaseViewCountIfEligible(int recipeBoardId, String ipAddress);

    //Recipe Steps Management
    List<RecipeBoardStepVO> getRecipeBoardStepsByBoardId(int recipeBoardId); // 레시피 스텝 목록 조회

    void saveRecipeSteps(int recipeBoardId, List<RecipeBoardStepVO> steps);

    RecipeBoardVO findRecipeById(int id);

    Map<String, Object> findAllRecipes(Pagination pagination, Integer typeId, Integer situationId, Integer methodId,
                                        String ingredientIds, String hashtag);

	RecipeDetailVO getRecipeDetailForUpdate(int recipeBoardId);


}