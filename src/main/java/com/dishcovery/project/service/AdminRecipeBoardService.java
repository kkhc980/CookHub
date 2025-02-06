package com.dishcovery.project.service;

import com.dishcovery.project.domain.*;
import com.dishcovery.project.util.Pagination;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface AdminRecipeBoardService {

    // Recipe CRUD
    RecipeBoardVO getByRecipeBoardId(int recipeBoardId);

    boolean createRecipe(RecipeBoardVO recipeBoard, List<Integer> ingredientIds,
                         String hashtags, MultipartFile thumbnail,
                         List<RecipeBoardStepVO> steps, List<RecipeIngredientsDetailVO> ingredientDetails) throws IOException;

    boolean updateRecipe(int recipeBoardId, RecipeBoardVO recipe, List<Integer> ingredientIds,
                         String hashtags, MultipartFile thumbnail,
                         List<RecipeBoardStepVO> steps, List<Integer> deleteStepIds, List<RecipeIngredientsDetailVO> ingredientDetails) throws IOException;

    boolean updateRecipe(int recipeBoardId, RecipeBoardVO recipe);

    boolean deleteRecipe(int recipeBoardId);

    // Recipe Details
    RecipeDetailVO getRecipeDetailById(int recipeBoardId);

    // Ingredients Management
    List<IngredientsVO> getAllIngredients();

    Set<Integer> getSelectedIngredientIdsByRecipeBoardId(int recipeBoardId);

    // Recipe Ingredient Details Management
    List<RecipeIngredientsDetailVO> getRecipeIngredientsDetailsByRecipeId(int recipeBoardId);

    // Hashtags Management
    List<HashtagsVO> getHashtagsByRecipeBoardId(int recipeBoardId);

    void updateHashtagsForRecipe(int recipeBoardId, String hashtags);

    List<String> getHashtagNamesByRecipeBoardId(int recipeBoardId);

    void deleteHashtagForRecipe(int recipeBoardId, int hashtagId);

    // Types, Methods, Situations
    List<TypesVO> getAllTypes();

    List<MethodsVO> getAllMethods();

    List<SituationsVO> getAllSituations();

    // Recipe Steps Management
    List<RecipeBoardStepVO> getRecipeBoardStepsByRecipeBoardId(int recipeBoardId);

    void updateRecipeSteps(int recipeBoardId, List<RecipeBoardStepVO> steps);

    // Pagination & Filtering
    Map<String, Object> getRecipeBoardListWithFilters(Pagination pagination);

    Pagination preprocessPagination(Pagination pagination);

    // Thumbnail Management
    Optional<Resource> getThumbnailByRecipeBoardId(int recipeBoardId);

    RecipeBoardVO findRecipeById(int recipeBoardId);

    Map<String, Object> findAllRecipes(Pagination pagination);

    boolean registerRecipe(RecipeBoardVO recipe);

    Map<String, Object> findAllRecipesWithFilters(Pagination pagination, Integer typeId, Integer situationId, Integer methodId,
                                               String ingredientIds, String hashtag);
    TypesVO getTypeById(int typeId);
    MethodsVO getMethodById(int methodId);
    SituationsVO getSituationById(int situationId);
    List<IngredientsVO> getIngredientsByRecipeId(int recipeBoardId);

	int getTotalRecipeCount();
}