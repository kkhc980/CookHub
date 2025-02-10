package com.dishcovery.project.service;

import com.dishcovery.project.domain.*;
import com.dishcovery.project.persistence.AdminRecipeBoardMapper;
import com.dishcovery.project.util.Pagination;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminRecipeBoardServiceImple implements AdminRecipeBoardService {

    private final AdminRecipeBoardMapper adminRecipeBoardMapper;

    @Value("${file.upload.path}") // application.properties or application.yml 에 정의
    private String fileUploadPath;

    // Recipe CRUD
    @Override
    public RecipeBoardVO getByRecipeBoardId(int recipeBoardId) {
        return adminRecipeBoardMapper.getByRecipeBoardId(recipeBoardId);
    }

    @Override
    @Transactional
    public boolean createRecipe(RecipeBoardVO recipeBoard, List<Integer> ingredientIds,
                                 String hashtags, MultipartFile thumbnail, List<RecipeBoardStepVO> steps,
                                 List<RecipeIngredientsDetailVO> ingredientDetails) throws IOException {

        // 1. 레시피 기본 정보 저장
        adminRecipeBoardMapper.insertRecipeBoard(recipeBoard);
        int recipeBoardId = recipeBoard.getRecipeBoardId(); // 자동 생성된 ID 가져오기

        // 2. 썸네일 저장 및 경로 업데이트
        if (thumbnail != null && !thumbnail.isEmpty()) {
            String thumbnailPath = saveThumbnail(recipeBoardId, thumbnail);
            recipeBoard.setThumbnailPath(thumbnailPath);
            adminRecipeBoardMapper.updateRecipeBoard(recipeBoard);
        }

        // 3. 재료 저장
        if (ingredientIds != null && !ingredientIds.isEmpty()) {
            for (Integer ingredientId : ingredientIds) {
                RecipeIngredientsVO recipeIngredientsVO = new RecipeIngredientsVO();
                recipeIngredientsVO.setRecipeBoardId(recipeBoardId);
                recipeIngredientsVO.setIngredientId(ingredientId);
                adminRecipeBoardMapper.insertRecipeIngredient(recipeIngredientsVO);
            }
        }

        // 4. 해시태그 저장
        updateHashtagsForRecipe(recipeBoardId, hashtags);

        // 5. 레시피 단계 저장
        updateRecipeSteps(recipeBoardId, steps);

        // 6. 레시피 재료 상세 정보 저장
        if (ingredientDetails != null && !ingredientDetails.isEmpty()) {
            for (RecipeIngredientsDetailVO detail : ingredientDetails) {
                detail.setRecipeBoardId(recipeBoardId);
                adminRecipeBoardMapper.insertRecipeIngredientsDetail(detail);
            }
        }

        return true; // 성공적으로 생성되었음을 반환
    }

    @Override
    @Transactional
    public boolean updateRecipe(int recipeBoardId, RecipeBoardVO recipe, List<Integer> ingredientIds,
                                 String hashtags, MultipartFile thumbnail, List<RecipeBoardStepVO> steps,
                                 List<Integer> deleteStepIds, List<RecipeIngredientsDetailVO> ingredientDetails) throws IOException {

        // 1. 레시피 기본 정보 업데이트
        recipe.setRecipeBoardId(recipeBoardId);
        adminRecipeBoardMapper.updateRecipeBoard(recipe);

        // 2. 썸네일 저장 및 경로 업데이트
        if (thumbnail != null && !thumbnail.isEmpty()) {
            String thumbnailPath = saveThumbnail(recipeBoardId, thumbnail);
            recipe.setThumbnailPath(thumbnailPath);
            adminRecipeBoardMapper.updateRecipeBoard(recipe); // 썸네일 경로 업데이트
        }

        // 3. 기존 재료 삭제 후 다시 저장
        adminRecipeBoardMapper.deleteRecipeIngredientsByRecipeId(recipeBoardId);
        if (ingredientIds != null && !ingredientIds.isEmpty()) {
            for (Integer ingredientId : ingredientIds) {
                RecipeIngredientsVO recipeIngredientsVO = new RecipeIngredientsVO();
                recipeIngredientsVO.setRecipeBoardId(recipeBoardId);
                recipeIngredientsVO.setIngredientId(ingredientId);
                adminRecipeBoardMapper.insertRecipeIngredient(recipeIngredientsVO);
            }
        }

        // 4. 해시태그 업데이트
        updateHashtagsForRecipe(recipeBoardId, hashtags);

        // 5. 레시피 단계 업데이트
        updateRecipeSteps(recipeBoardId, steps);
        if (deleteStepIds != null && !deleteStepIds.isEmpty()) {
            for (Integer stepId : deleteStepIds) {
                adminRecipeBoardMapper.deleteRecipeBoardStepByStepId(stepId);
            }
        }

        // 6. 레시피 재료 상세 정보 업데이트
        adminRecipeBoardMapper.deleteRecipeIngredientsDetailsByRecipeId(recipeBoardId);
        if (ingredientDetails != null && !ingredientDetails.isEmpty()) {
            for (RecipeIngredientsDetailVO detail : ingredientDetails) {
                detail.setRecipeBoardId(recipeBoardId);
                adminRecipeBoardMapper.insertRecipeIngredientsDetail(detail);
            }
        }

        return true; // 성공적으로 업데이트되었음을 반환
    }

    @Override
    public boolean updateRecipe(int recipeBoardId, RecipeBoardVO recipe) {
        recipe.setRecipeBoardId(recipeBoardId);
        adminRecipeBoardMapper.updateRecipeBoard(recipe);
        return true;
    }

    @Override
    public boolean deleteRecipe(int recipeBoardId) {
        // 관련 데이터 삭제: 재료, 해시태그, 레시피 단계, 재료 상세 정보
        adminRecipeBoardMapper.deleteRecipeIngredientsByRecipeId(recipeBoardId);
        adminRecipeBoardMapper.deleteRecipeHashtagsByRecipeBoardId(recipeBoardId);
        adminRecipeBoardMapper.deleteRecipeBoardStepsByRecipeBoardId(recipeBoardId);
        adminRecipeBoardMapper.deleteRecipeIngredientsDetailsByRecipeId(recipeBoardId);

        // 레시피 삭제
        adminRecipeBoardMapper.deleteRecipeBoard(recipeBoardId);
        return true;
    }

    // Recipe Details
    @Override
    public RecipeDetailVO getRecipeDetailById(int recipeBoardId) {
        RecipeBoardVO recipe = adminRecipeBoardMapper.getByRecipeBoardId(recipeBoardId);
        List<IngredientsVO> ingredients = adminRecipeBoardMapper.getIngredientsByRecipeId(
                recipeBoardId);
        List<RecipeIngredientsDetailVO> ingredientDetails = adminRecipeBoardMapper
                .getIngredientsDetailByRecipeId(recipeBoardId);
        List<HashtagsVO> hashtags = getHashtagsByRecipeBoardId(recipeBoardId);
        List<RecipeBoardStepVO> steps = adminRecipeBoardMapper
                .selectRecipeBoardStepsByRecipeBoardId(recipeBoardId);

        // RecipeDetailVO 생성 및 데이터 설정
        RecipeDetailVO recipeDetail = new RecipeDetailVO();
        recipeDetail.setRecipeBoard(recipe);
        recipeDetail.setIngredients(ingredients);
        recipeDetail.setIngredientDetails(ingredientDetails);
        recipeDetail.setHashtags(hashtags);
        recipeDetail.setRecipeSteps(steps);

        return recipeDetail;
    }

    // Ingredients Management
    @Override
    public List<IngredientsVO> getAllIngredients() {
        return adminRecipeBoardMapper.getAllIngredients();
    }

    @Override
    public Set<Integer> getSelectedIngredientIdsByRecipeBoardId(int recipeBoardId) {
        List<IngredientsVO> ingredients = adminRecipeBoardMapper.getIngredientsByRecipeId(
                recipeBoardId);
        if (ingredients == null) {
            return Collections.emptySet(); // null 대신 빈 Set 반환
        }
        return ingredients.stream()
                .filter(Objects::nonNull) // null 요소 필터링
                .map(IngredientsVO::getIngredientId)
                .collect(Collectors.toSet());
    }

    // Recipe Ingredient Details Management
    @Override
    public List<RecipeIngredientsDetailVO> getRecipeIngredientsDetailsByRecipeId(
            int recipeBoardId) {
        return adminRecipeBoardMapper.getIngredientsDetailByRecipeId(recipeBoardId);
    }

    // Hashtags Management
    @Override
    public List<HashtagsVO> getHashtagsByRecipeBoardId(int recipeBoardId) {
        // hashtagVO로 변경해서 가져오기
        List<String> hashtagNames = adminRecipeBoardMapper.getHashtagNamesByRecipeId(recipeBoardId);
        return hashtagNames.stream()
                .map(hashtagName -> {
                    HashtagsVO hashtag = new HashtagsVO();
                    hashtag.setHashtagName(hashtagName);
                    return hashtag;
                })
                .collect(Collectors.toList());
    }
    @Override
    public void updateHashtagsForRecipe(int recipeBoardId, String hashtags) {
        // 1. 기존 해시태그 삭제
        adminRecipeBoardMapper.deleteRecipeHashtagsByRecipeBoardId(recipeBoardId);

        // 2. 새로운 해시태그 저장
        if (hashtags != null && !hashtags.isEmpty()) {
            String[] hashtagArray = hashtags.split("#");
            for (String hashtagName : hashtagArray) {
                if (!hashtagName.trim().isEmpty()) {
                    // 3. 해시태그가 이미 존재하는지 확인
                    HashtagsVO existingHashtag = adminRecipeBoardMapper.getHashtagByName(
                            hashtagName.trim());
                    int hashtagId;

                    if (existingHashtag == null) {
                        // 4. 존재하지 않으면 새로 생성
                        HashtagsVO newHashtag = new HashtagsVO();
                        newHashtag.setHashtagName(hashtagName.trim());
                        hashtagId = adminRecipeBoardMapper.getNextHashtagId();
                        newHashtag.setHashtagId(hashtagId);
                        adminRecipeBoardMapper.insertHashtag(newHashtag);
                    } else {
                        // 5. 존재하면 기존 ID 사용
                        hashtagId = existingHashtag.getHashtagId();
                    }

                    // 6. 레시피-해시태그 연결 테이블에 저장
                    RecipeHashtagsVO recipeHashtag = new RecipeHashtagsVO();
                    recipeHashtag.setRecipeBoardId(recipeBoardId);
                    recipeHashtag.setHashtagId(hashtagId);
                    adminRecipeBoardMapper.insertRecipeHashtag(recipeHashtag);
                }
            }
        }
    }

    @Override
    public List<String> getHashtagNamesByRecipeBoardId(int recipeBoardId) {
        return adminRecipeBoardMapper.getHashtagNamesByRecipeId(recipeBoardId);
    }

    @Override
    public void deleteHashtagForRecipe(int recipeBoardId, int hashtagId) {
        // 레시피-해시태그 연결 테이블에서 해당 해시태그 삭제
        adminRecipeBoardMapper.deleteRecipeHashtagsByRecipeBoardIdAndHashtagId(recipeBoardId,
                hashtagId);

        // 해당 해시태그가 다른 레시피에도 사용되는지 확인
        int recipeCount = adminRecipeBoardMapper.getRecipeCountByHashtagId(hashtagId);
        if (recipeCount == 0) {
            // 다른 레시피에 사용되지 않으면 해시태그 자체 삭제
            adminRecipeBoardMapper.deleteHashtagByHashtagId(hashtagId);
        }
    }

    // Types, Methods, Situations
    @Override
    public List<TypesVO> getAllTypes() {
        return adminRecipeBoardMapper.getAllTypes();
    }

    @Override
    public List<MethodsVO> getAllMethods() {
        return adminRecipeBoardMapper.getAllMethods();
    }

    @Override
    public List<SituationsVO> getAllSituations() {
        return adminRecipeBoardMapper.getAllSituations();
    }

    // Recipe Steps Management
    @Override
    public List<RecipeBoardStepVO> getRecipeBoardStepsByRecipeBoardId(int recipeBoardId) {
        return adminRecipeBoardMapper.selectRecipeBoardStepsByRecipeBoardId(recipeBoardId);
    }

    @Override
    public void updateRecipeSteps(int recipeBoardId, List<RecipeBoardStepVO> steps) {
        // 1. 기존 레시피 단계 삭제
        adminRecipeBoardMapper.deleteRecipeBoardStepsByRecipeBoardId(recipeBoardId);

        // 2. 새로운 레시피 단계 저장
        if (steps != null && !steps.isEmpty()) {
            for (RecipeBoardStepVO step : steps) {
                step.setRecipeBoardId(recipeBoardId);
                adminRecipeBoardMapper.insertRecipeBoardStep(step);
            }
        }
    }

    // Pagination & Filtering
    @Override
    public Map<String, Object> getRecipeBoardListWithFilters(Pagination pagination) {
        // 1. Pagination 정보 전처리 (필요한 경우)
        pagination = preprocessPagination(pagination);

        // 2. 필터링 및 페이징을 적용한 레시피 목록 조회
        List<RecipeBoardVO> recipeList = adminRecipeBoardMapper.selectAllRecipes(pagination);

        // 3. 필터링 조건에 맞는 전체 레시피 수 조회
        int totalCount = adminRecipeBoardMapper.getTotalCount(pagination);

        // 4. 결과 Map 생성 및 반환
        Map<String, Object> result = new HashMap<>();
        result.put("recipeList", recipeList);
        result.put("totalCount", totalCount);

        return result;
    }

    @Override
    public Pagination preprocessPagination(Pagination pagination) {
        // Pagination 관련 정보 검증 및 수정 로직 (예: pageNum, amount 검증)
        // 필요에 따라 구현
        return pagination;
    }

    // Thumbnail Management
    @Override
    public Optional<Resource> getThumbnailByRecipeBoardId(int recipeBoardId) {
        RecipeBoardVO recipe = adminRecipeBoardMapper.getByRecipeBoardId(recipeBoardId);
        if (recipe == null || recipe.getThumbnailPath() == null) {
            return Optional.empty();
        }

        File file = new File(recipe.getThumbnailPath());
        if (!file.exists()) {
            return Optional.empty();
        }

        Resource resource = new FileSystemResource(file);
        return Optional.of(resource);
    }

    private String saveThumbnail(int recipeBoardId, MultipartFile thumbnail) throws IOException {
        File uploadDir = new File(fileUploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        String originalFileName = thumbnail.getOriginalFilename();
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String newFileName = "recipe_" + recipeBoardId + "_" + System.currentTimeMillis() + fileExtension;
        File destFile = new File(uploadDir, newFileName);

        thumbnail.transferTo(destFile);
        return destFile.getAbsolutePath(); // 파일의 절대 경로 반환
    }

    @Override
    public RecipeBoardVO findRecipeById(int recipeBoardId) {
        return adminRecipeBoardMapper.getByRecipeBoardId(recipeBoardId);
    }

    @Override
    public Map<String, Object> findAllRecipes(Pagination pagination) {
        List<RecipeBoardVO> recipeList = adminRecipeBoardMapper.selectAllRecipes(pagination);
        int totalCount = adminRecipeBoardMapper.getTotalCount(pagination);

        Map<String, Object> result = new HashMap<>();
        result.put("recipeList", recipeList);
        result.put("totalCount", totalCount);
        return result;
    }

    @Override
    public boolean registerRecipe(RecipeBoardVO recipe) {
        adminRecipeBoardMapper.insertRecipeBoard(recipe);
        return true;
    }

    @Override
    public Map<String, Object> findAllRecipesWithFilters(Pagination pagination, Integer typeId,
                                                            Integer situationId, Integer methodId, String ingredientIds, String hashtag) {
        // 1. Pagination 정보 전처리 (필요한 경우)
        pagination = preprocessPagination(pagination);

        // 2. 필터링 조건 설정
        if (typeId != null) {
            pagination.setTypeId(typeId);
        }
        if (situationId != null) {
            pagination.setSituationId(situationId);
        }
        if (methodId != null) {
            pagination.setMethodId(methodId);
        }
        if (ingredientIds != null && !ingredientIds.isEmpty()) {
            List<Integer> ingredientIdList = Arrays.stream(ingredientIds.split(","))
                    .map(Integer::parseInt).collect(Collectors.toList());
            pagination.setIngredientIds(ingredientIdList);
        }
        if (hashtag != null && !hashtag.isEmpty()) {
            pagination.setHashtag(hashtag);
        }

        // 3. 필터링 및 페이징을 적용한 레시피 목록 조회
        List<RecipeBoardVO> recipeList = adminRecipeBoardMapper.selectAllRecipes(pagination);

        // 4. 필터링 조건에 맞는 전체 레시피 수 조회
        int totalCount = adminRecipeBoardMapper.getTotalCount(pagination);

        // 5. 결과 Map 생성 및 반환
        Map<String, Object> result = new HashMap<>();
        result.put("recipeList", recipeList);
        result.put("totalCount", totalCount);

        return result;
    }

     @Override
    public TypesVO getTypeById(int typeId) {
        return adminRecipeBoardMapper.getTypeById(typeId);
    }

    @Override
    public MethodsVO getMethodById(int methodId) {
        return adminRecipeBoardMapper.getMethodById(methodId);
    }

    @Override
    public SituationsVO getSituationById(int situationId) {
        return adminRecipeBoardMapper.getSituationById(situationId);
    }
    
     @Override
    public List<IngredientsVO> getIngredientsByRecipeId(int recipeBoardId) {
        return adminRecipeBoardMapper.getIngredientsByRecipeId(recipeBoardId);
    }

	@Override
	public int getTotalRecipeCount() {
		// TODO Auto-generated method stub
		return 0;
	}
}