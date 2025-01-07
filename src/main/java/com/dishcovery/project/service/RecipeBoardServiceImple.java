package com.dishcovery.project.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.dishcovery.project.domain.*;
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
        if (thumbnail == null || thumbnail.isEmpty()) {
            throw new IllegalArgumentException("Thumbnail is required for creating a recipe.");
        }

        try {
            // 썸네일 저장
            String thumbnailPath = saveThumbnail(thumbnail);
            recipeBoard.setThumbnailPath(thumbnailPath);

            // 게시글 ID 생성 및 저장
            int nextId = mapper.getNextRecipeBoardId();
            recipeBoard.setRecipeBoardId(nextId);
            mapper.insertRecipeBoard(recipeBoard);

            // 재료 정보 추가
            addIngredientsToRecipe(nextId, ingredientIds);

        } catch (Exception e) {
            throw new RuntimeException("Failed to create recipe with thumbnail", e);
        }
    }

    @Override
    @Transactional
    public void updateRecipeWithIngredients(RecipeBoardVO recipeBoard, List<Integer> ingredientIds, MultipartFile thumbnail) {
        if (thumbnail == null || thumbnail.isEmpty()) {
            throw new IllegalArgumentException("Thumbnail is required for updating a recipe.");
        }

        try {
            // 기존 썸네일 삭제 및 새 썸네일 저장
            RecipeBoardVO existingRecipe = getByRecipeBoardId(recipeBoard.getRecipeBoardId());
            if (existingRecipe != null && existingRecipe.getThumbnailPath() != null) {
                FileUploadUtil.deleteFile("C:/uploads", existingRecipe.getThumbnailPath());
            }

            String thumbnailPath = saveThumbnail(thumbnail);
            recipeBoard.setThumbnailPath(thumbnailPath);

            // 레시피와 재료 정보 업데이트
            mapper.updateRecipeBoard(recipeBoard);
            mapper.deleteRecipeIngredientsByRecipeId(recipeBoard.getRecipeBoardId());
            addIngredientsToRecipe(recipeBoard.getRecipeBoardId(), ingredientIds);

        } catch (Exception e) {
            throw new RuntimeException("Failed to update recipe with thumbnail", e);
        }
    }

    @Override
    @Transactional
    public void deleteRecipe(int recipeBoardId) {
        try {
            RecipeBoardVO recipeBoard = getByRecipeBoardId(recipeBoardId);

            // Thumbnail 삭제
            deleteThumbnail(recipeBoard.getThumbnailPath());

            // Recipe 및 재료 삭제
            mapper.deleteRecipeIngredientsByRecipeId(recipeBoardId);
            mapper.deleteRecipeBoard(recipeBoardId);

        } catch (Exception e) {
            log.error("Failed to delete recipe", e);
            throw new RuntimeException("Failed to delete recipe", e);
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
    public Set<Integer> getSelectedIngredientIdsByRecipeBoardId(int recipeBoardId) {
        log.info("Fetching selected ingredient IDs for recipe ID: " + recipeBoardId);
        return mapper.getIngredientsByRecipeId(recipeBoardId)
                     .stream()
                     .map(IngredientsVO::getIngredientId)
                     .collect(Collectors.toSet());
    }

    @Override
    public Pagination preprocessPagination(Pagination pagination) {
        if (pagination.getIngredientIds() != null && pagination.getIngredientIds().contains(1)) {
            pagination.setIngredientIds(null);
        }
        if (pagination.getTypeId() != null && pagination.getTypeId() == 1) {
            pagination.setTypeId(null);
        }
        if (pagination.getMethodId() != null && pagination.getMethodId() == 1) {
            pagination.setMethodId(null);
        }
        if (pagination.getSituationId() != null && pagination.getSituationId() == 1) {
            pagination.setSituationId(null);
        }
        
        return pagination;
    }

    @Override
    public Map<String, Object> getRecipeBoardListWithFilters(Pagination pagination) {
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
    public Optional<Resource> getThumbnailByRecipeBoardId(int recipeBoardId) {
        try {
            RecipeBoardVO recipeBoard = getByRecipeBoardId(recipeBoardId);

            if (recipeBoard == null || recipeBoard.getThumbnailPath() == null) {
                return Optional.empty();
            }

            File file = new File("C:/uploads/" + recipeBoard.getThumbnailPath());
            if (!file.exists()) {
                return Optional.empty();
            }

            return Optional.of(new FileSystemResource(file));
        } catch (Exception e) {
            log.error("Failed to fetch thumbnail", e);
            return Optional.empty();
        }
    }

    private String saveThumbnail(MultipartFile thumbnail) throws IOException {
        String uuid = UUID.randomUUID().toString();
        String extension = FileUploadUtil.subStrExtension(thumbnail.getOriginalFilename());
        String savedFileName = uuid + "." + extension;

        String datePath = FileUploadUtil.makeDatePath().replace("\\", "/");
        FileUploadUtil.saveFile("C:/uploads", thumbnail, savedFileName);

        return datePath + "/" + savedFileName;
    }

    private void deleteThumbnail(String thumbnailPath) {
        if (thumbnailPath != null) {
            FileUploadUtil.deleteFile("C:/uploads", thumbnailPath);
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
}
