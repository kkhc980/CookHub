package com.dishcovery.project.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.dishcovery.project.domain.IngredientsVO;
import com.dishcovery.project.domain.MethodsVO;
import com.dishcovery.project.domain.RecipeBoardVO;
import com.dishcovery.project.domain.RecipeDetailVO;
import com.dishcovery.project.domain.RecipeIngredientsVO;
import com.dishcovery.project.domain.SituationsVO;
import com.dishcovery.project.domain.TypesVO;
import com.dishcovery.project.persistence.RecipeBoardMapper;
import com.dishcovery.project.util.FileUploadUtil;
import com.dishcovery.project.util.PageMaker;
import com.dishcovery.project.util.Pagination;

import lombok.extern.log4j.Log4j;

@Service
@Log4j
public class RecipeBoardServiceImple implements RecipeBoardService {

    @Autowired
    private RecipeBoardMapper mapper;

    @Override
    public RecipeBoardVO getByRecipeBoardId(int recipeBoardId) {
        log.info("Fetching recipe board entry with ID: " + recipeBoardId);
        return mapper.getByRecipeBoardId(recipeBoardId);
    }

    @Override
    @Transactional
    public void createRecipeWithIngredients(RecipeBoardVO recipeBoard, List<Integer> ingredientIds, MultipartFile thumbnail) {
        try {
            // 썸네일 파일 처리
            if (thumbnail != null && !thumbnail.isEmpty()) {
                String uuid = UUID.randomUUID().toString();
                String extension = FileUploadUtil.subStrExtension(thumbnail.getOriginalFilename());
                String savedFileName = uuid + "." + extension;

                // 날짜 기반 폴더 생성 및 파일 저장
                String datePath = FileUploadUtil.makeDatePath().replace("\\", "/");
                FileUploadUtil.saveFile("C:/uploads", thumbnail, savedFileName);

                // 이미지 경로 저장
                recipeBoard.setThumbnailPath(datePath + "/" + savedFileName);
            } else {
                log.info("No thumbnail uploaded");
            }

            // 게시글 ID 생성 및 저장
            int nextId = mapper.getNextRecipeBoardId();
            recipeBoard.setRecipeBoardId(nextId);
            mapper.insertRecipeBoard(recipeBoard);

            // 재료 정보 추가
            addIngredientsToRecipe(nextId, ingredientIds);

        } catch (Exception e) {
            throw new RuntimeException("Failed to register recipe", e);
        }
    }

    private void addIngredientsToRecipe(int recipeBoardId, List<Integer> ingredientIds) {
        if (ingredientIds != null && !ingredientIds.isEmpty()) {
            ingredientIds.forEach(ingredientId -> {
                RecipeIngredientsVO recipeIngredient = new RecipeIngredientsVO();
                recipeIngredient.setRecipeBoardId(recipeBoardId);
                recipeIngredient.setIngredientId(ingredientId);
                mapper.insertRecipeIngredient(recipeIngredient);
            });
        }
    }

    @Override
    public RecipeDetailVO getRecipeDetailById(int recipeBoardId) {
        log.info("Fetching recipe detail for ID: " + recipeBoardId);

        RecipeBoardVO recipeBoard = mapper.getByRecipeBoardId(recipeBoardId);
        if (recipeBoard == null) return null;

        RecipeDetailVO detail = new RecipeDetailVO();
        detail.setRecipeBoard(recipeBoard);
        detail.setTypeName(mapper.getTypeName(recipeBoard.getTypeId()));
        detail.setMethodName(mapper.getMethodName(recipeBoard.getMethodId()));
        detail.setSituationName(mapper.getSituationName(recipeBoard.getSituationId()));
        detail.setIngredients(mapper.getIngredientsByRecipeId(recipeBoardId));

        return detail;
    }

    @Override
    public List<IngredientsVO> getIngredientsByRecipeId(int recipeBoardId) {
        log.info("Fetching ingredients for recipe ID: " + recipeBoardId);
        return mapper.getIngredientsByRecipeId(recipeBoardId);
    }

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
    public List<IngredientsVO> getAllIngredients() {
        log.info("Fetching all ingredients");
        return mapper.getAllIngredients();
    }

    @Override
    @Transactional
    public void updateRecipeWithIngredients(RecipeBoardVO recipeBoard, List<Integer> ingredientIds, MultipartFile thumbnail) {
    	try {
            // 기존 썸네일 파일 삭제 및 새 썸네일 저장
            if (thumbnail != null && !thumbnail.isEmpty()) {
                // 기존 썸네일 삭제
                RecipeBoardVO existingRecipe = getByRecipeBoardId(recipeBoard.getRecipeBoardId());
                if (existingRecipe != null && existingRecipe.getThumbnailPath() != null) {
                    FileUploadUtil.deleteFile("C:/uploads", existingRecipe.getThumbnailPath());
                }

                // 새 썸네일 저장
                String uuid = UUID.randomUUID().toString();
                String extension = FileUploadUtil.subStrExtension(thumbnail.getOriginalFilename());
                String savedFileName = uuid + "." + extension;

                String datePath = FileUploadUtil.makeDatePath();
                FileUploadUtil.saveFile("C:/uploads", thumbnail, savedFileName);

                recipeBoard.setThumbnailPath(datePath + "/" + savedFileName);
            }

            log.info("Updating recipe with ID: " + recipeBoard.getRecipeBoardId());
            // 레시피와 재료 정보 업데이트
            mapper.updateRecipeBoard(recipeBoard);
            mapper.deleteRecipeIngredientsByRecipeId(recipeBoard.getRecipeBoardId());
            if (ingredientIds != null && !ingredientIds.isEmpty()) {
                addIngredientsToRecipe(recipeBoard.getRecipeBoardId(), ingredientIds);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to update recipe", e);
        }
    }

    @Override
    @Transactional
    public void deleteRecipe(int recipeBoardId) {
    	 try {
    	        // 썸네일 파일 삭제
    	        RecipeBoardVO recipeBoard = getByRecipeBoardId(recipeBoardId);
    	        if (recipeBoard != null && recipeBoard.getThumbnailPath() != null) {
    	            FileUploadUtil.deleteFile("C:/uploads", recipeBoard.getThumbnailPath());
    	        }
    	
    	log.info("Deleting recipe with ID: " + recipeBoardId);

        mapper.deleteRecipeIngredientsByRecipeId(recipeBoardId);
        mapper.deleteRecipeBoard(recipeBoardId);
    	 } catch (Exception e) {
    	        throw new RuntimeException("Failed to delete recipe", e);
    	    }
    }

    @Override
    public Set<Integer> getSelectedIngredientIdsByRecipeId(int recipeBoardId) {
        log.info("Fetching selected ingredient IDs for recipe ID: " + recipeBoardId);
        return mapper.getIngredientsByRecipeId(recipeBoardId)
                     .stream()
                     .map(IngredientsVO::getIngredientId)
                     .collect(Collectors.toSet());
    }

    @Override
    public Pagination preprocessPagination(Pagination pagination) {
        log.info("Preprocessing pagination filters");

        if (pagination.getIngredientIds() != null && pagination.getIngredientIds().contains(1)) {
            pagination.setIngredientIds(null); // "전체"는 필터에서 제외
        }
        if (pagination.getTypeId() != null && pagination.getTypeId() == 1) {
            pagination.setTypeId(null); // "전체"는 필터에서 제외
        }
        if (pagination.getMethodId() != null && pagination.getMethodId() == 1) {
            pagination.setMethodId(null); // "전체"는 필터에서 제외
        }
        if (pagination.getSituationId() != null && pagination.getSituationId() == 1) {
            pagination.setSituationId(null); // "전체"는 필터에서 제외
        }

        return pagination;
    }

    @Override
    public Map<String, Object> getRecipeBoardListWithFilters(Pagination pagination) {
        log.info("Fetching recipe board list with filters");

        Map<String, Object> result = new HashMap<>();
        result.put("recipeList", mapper.getRecipeBoardListWithPaging(pagination));
        result.put("allIngredients", getAllIngredients());
        result.put("allTypes", getAllTypes());
        result.put("allMethods", getAllMethods());
        result.put("allSituations", getAllSituations());

        int totalCount = mapper.getTotalCountWithFilters(pagination);
        PageMaker pageMaker = new PageMaker();
        pageMaker.setPagination(pagination);
        pageMaker.setTotalCount(totalCount);

        result.put("pageMaker", pageMaker);

        return result;
    }
    
    @Override
    public Resource getThumbnailByRecipeBoardId(int recipeBoardId) {
        try {
            RecipeBoardVO recipeBoard = getByRecipeBoardId(recipeBoardId);
            if (recipeBoard == null) {
                log.error("RecipeBoard not found for ID: " + recipeBoardId);
                throw new FileNotFoundException("RecipeBoard not found for ID: " + recipeBoardId);
            }

            if (recipeBoard.getThumbnailPath() == null) {
                log.error("Thumbnail path is null for recipe ID: " + recipeBoardId);
                throw new FileNotFoundException("Thumbnail not found for recipe ID: " + recipeBoardId);
            }

            String filePath = "C:/uploads/" + recipeBoard.getThumbnailPath().replace("\\", "/");
            log.info("Thumbnail file path: " + filePath);

            File file = new File(filePath);
            if (!file.exists()) {
                log.error("Thumbnail file does not exist: " + filePath);
                throw new FileNotFoundException("Thumbnail file does not exist: " + filePath);
            }

            return new FileSystemResource(file);
        } catch (IOException e) {
            log.error("Error fetching thumbnail for recipe ID: " + recipeBoardId, e);
            throw new RuntimeException("Error fetching thumbnail", e);
        }
    }
}
