package com.dishcovery.project.service;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.dishcovery.project.domain.HashtagsVO;
import com.dishcovery.project.domain.IngredientsVO;
import com.dishcovery.project.domain.MethodsVO;
import com.dishcovery.project.domain.RecipeBoardVO;
import com.dishcovery.project.domain.RecipeBoardViewLogVO;
import com.dishcovery.project.domain.RecipeDetailVO;
import com.dishcovery.project.domain.RecipeHashtagsVO;
import com.dishcovery.project.domain.RecipeIngredientsVO;
import com.dishcovery.project.domain.SituationsVO;
import com.dishcovery.project.domain.TypesVO;
import com.dishcovery.project.persistence.RecipeBoardMapper;
import com.dishcovery.project.persistence.RecipeRankingMapper;
import com.dishcovery.project.persistence.RecipeViewStatsMapper;
import com.dishcovery.project.util.FileUploadUtil;
import com.dishcovery.project.util.PageMaker;
import com.dishcovery.project.util.Pagination;

import lombok.extern.log4j.Log4j;

@Service
@Log4j
public class RecipeBoardServiceImple implements RecipeBoardService {

    @Autowired
    private RecipeBoardMapper mapper;

    @Autowired
    private RecipeViewStatsMapper viewStatsMapper;

    @Autowired
    private RecipeRankingMapper rankingMapper;
    @Override
    public RecipeBoardVO getByRecipeBoardId(int recipeBoardId) {
        log.info("Fetching recipe board entry with ID: " + recipeBoardId);
        return mapper.getByRecipeBoardId(recipeBoardId);
    }

    @Override
    @Transactional
    public void createRecipe(RecipeBoardVO recipeBoard, List<Integer> ingredientIds, String hashtags, MultipartFile thumbnail) {
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

            // RecipeViewStats 초기화 (insertInitialViewStats 호출)
            viewStatsMapper.insertInitialViewStats(nextId);
            // 재료 정보 추가
            addIngredientsToRecipe(nextId, ingredientIds);

            // 해시태그 처리
            saveHashtagsForRecipe(nextId, hashtags);

        } catch (Exception e) {
            throw new RuntimeException("Failed to create recipe with thumbnail and hashtags", e);
        }
    }

    @Override
    @Transactional
    public void updateRecipe(RecipeBoardVO recipeBoard, List<Integer> ingredientIds, String hashtags, MultipartFile thumbnail) {
        try {
        	// 기존 레시피 정보 가져오기
            RecipeBoardVO existingRecipe = getByRecipeBoardId(recipeBoard.getRecipeBoardId());
            if (existingRecipe == null) {
                throw new IllegalArgumentException("Recipe not found with ID: " + recipeBoard.getRecipeBoardId());
            }

            // 썸네일 처리
            if (thumbnail == null || thumbnail.isEmpty()) {
                // 파일이 선택되지 않았을 경우 기존 썸네일 유지
                recipeBoard.setThumbnailPath(existingRecipe.getThumbnailPath());
            } else {
                // 기존 썸네일 삭제
                if (existingRecipe.getThumbnailPath() != null) {
                    FileUploadUtil.deleteFile("C:/uploads", existingRecipe.getThumbnailPath());
                }
                // 새 썸네일 저장
                String thumbnailPath = saveThumbnail(thumbnail);
                recipeBoard.setThumbnailPath(thumbnailPath);
            }

            // 레시피 업데이트
            mapper.updateRecipeBoard(recipeBoard);

            // 기존 재료 정보 삭제 및 추가
            mapper.deleteRecipeIngredientsByRecipeId(recipeBoard.getRecipeBoardId());
            addIngredientsToRecipe(recipeBoard.getRecipeBoardId(), ingredientIds);


            // 기존 해시태그 가져오기
            List<String> existingHashtags = mapper.getHashtagNamesByRecipeId(recipeBoard.getRecipeBoardId());

            // 새롭게 전달된 해시태그 리스트 생성
            List<String> newHashtags = hashtags != null ? Arrays.asList(hashtags.split(",")) : List.of();

            List<String> hashtagsToRemove = existingHashtags.stream()
            	    .filter(tag -> !newHashtags.contains(tag))
            	    .collect(Collectors.toList());

            	List<String> hashtagsToAdd = newHashtags.stream()
            	    .filter(tag -> !existingHashtags.contains(tag))
            	    .collect(Collectors.toList());

            // 해시태그 추가 및 삭제
            addHashtagsToRecipe(recipeBoard.getRecipeBoardId(), hashtagsToAdd);
            removeHashtagsFromRecipe(recipeBoard.getRecipeBoardId(), hashtagsToRemove);

        } catch (Exception e) {
            throw new RuntimeException("Failed to update recipe with hashtags", e);
        }
    }

    @Override
    @Transactional
    public void saveHashtagsForRecipe(int recipeBoardId, String hashtags) {
        try {
            // 기존 해시태그 연결 삭제
            mapper.deleteRecipeHashtagsByRecipeId(recipeBoardId);

            if (hashtags == null || hashtags.isBlank()) {
                return;
            }

            // 쉼표로 구분된 해시태그를 처리
            String[] hashtagArray = hashtags.split(",");
            for (String hashtagName : hashtagArray) {
                hashtagName = hashtagName.trim();

                if (!hashtagName.isEmpty()) {
                    // 해시태그 이름으로 검색
                    HashtagsVO existingHashtag = mapper.getHashtagByName(hashtagName);

                    if (existingHashtag == null) {
                        // 시퀀스를 사용해 새 해시태그 추가
                        int nextHashtagId = mapper.getNextHashtagId(); // 시퀀스 호출 메서드
                        HashtagsVO newHashtag = new HashtagsVO();
                        newHashtag.setHashtagId(nextHashtagId);
                        newHashtag.setHashtagName(hashtagName);

                        mapper.insertHashtag(newHashtag); // 새 해시태그 삽입
                        existingHashtag = newHashtag;
                    }

                    // Recipe-Hashtag 연결 추가
                    RecipeHashtagsVO recipeHashtag = new RecipeHashtagsVO();
                    recipeHashtag.setRecipeBoardId(recipeBoardId);
                    recipeHashtag.setHashtagId(existingHashtag.getHashtagId());
                    mapper.insertRecipeHashtag(recipeHashtag);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to save hashtags for recipe", e);
        }
    }

    @Override
    public List<HashtagsVO> getHashtagsByRecipeBoardId(int recipeBoardId) {
        log.info("Fetching hashtags for recipe ID: " + recipeBoardId);
        return mapper.getHashtagsByRecipeId(recipeBoardId);
    }

    @Override
    @Transactional
    public void deleteRecipe(int recipeBoardId) {
        try {
            // 게시글에 연결된 해시태그 정보 가져오기
            List<HashtagsVO> hashtags = mapper.getHashtagsByRecipeId(recipeBoardId);
            RecipeBoardVO existingRecipe = getByRecipeBoardId(recipeBoardId);
            if (existingRecipe.getThumbnailPath() != null) {
                deleteThumbnail(existingRecipe.getThumbnailPath());
            }
            // 게시글 삭제 (해시태그 관계 포함)
            mapper.deleteRecipeViewLogsByRecipeId(recipeBoardId);
            mapper.deleteRecipeHashtagsByRecipeId(recipeBoardId);
            mapper.deleteRecipeIngredientsByRecipeId(recipeBoardId);
            mapper.deleteRecipeBoard(recipeBoardId);
            rankingMapper.reorderRankPositions();
            
            // 다른 게시글과 연결되지 않은 해시태그 삭제
            for (HashtagsVO hashtag : hashtags) {
                int count = mapper.getRecipeCountByHashtagId(hashtag.getHashtagId());
                if (count == 0) { // 다른 게시글과 연결되지 않은 경우
                    mapper.deleteHashtagById(hashtag.getHashtagId());
                }
            }

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
        detail.setHashtags(mapper.getHashtagsByRecipeId(recipeBoardId));
        
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
    
    @Override
    public List<String> getHashtagNamesByRecipeBoardId(int recipeBoardId) {
        // 해시태그 VO 리스트를 가져오고, 이름 리스트로 변환
        return mapper.getHashtagsByRecipeId(recipeBoardId).stream()
                     .map(HashtagsVO::getHashtagName)
                     .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void addHashtagsToRecipe(int recipeBoardId, List<String> hashtagsToAdd) {
        for (String hashtagName : hashtagsToAdd) {
            // 유효성 검사: null 또는 빈 문자열은 제외
            if (hashtagName == null || hashtagName.trim().isEmpty()) {
                continue;
            }

            // 기존 해시태그가 있는지 확인
            HashtagsVO existingHashtag = mapper.getHashtagByName(hashtagName.trim());
            int hashtagId;

            if (existingHashtag == null) {
                // 해시태그가 없으면 새로 추가
                hashtagId = mapper.getNextHashtagId();
                HashtagsVO newHashtag = new HashtagsVO();
                newHashtag.setHashtagId(hashtagId);
                newHashtag.setHashtagName(hashtagName.trim());
                mapper.insertHashtag(newHashtag);
            } else {
                // 기존 해시태그 사용
                hashtagId = existingHashtag.getHashtagId();
            }

            // Recipe-Hashtag 연결 추가
            RecipeHashtagsVO recipeHashtag = new RecipeHashtagsVO();
            recipeHashtag.setRecipeBoardId(recipeBoardId);
            recipeHashtag.setHashtagId(hashtagId);
            mapper.insertRecipeHashtag(recipeHashtag);
        }
    }

    @Override
    @Transactional
    public void removeHashtagsFromRecipe(int recipeBoardId, List<String> hashtagsToRemove) {
        for (String hashtagName : hashtagsToRemove) {
            // 해시태그 ID 가져오기
            HashtagsVO existingHashtag = mapper.getHashtagByName(hashtagName);
            if (existingHashtag != null) {
                int hashtagId = existingHashtag.getHashtagId();

                // Recipe-Hashtag 관계 삭제
                mapper.deleteRecipeHashtagsByRecipeIdAndHashtagId(recipeBoardId, hashtagId);

                // 해당 해시태그가 다른 Recipe와 연결되지 않았다면 해시태그 삭제
                int recipeCount = mapper.getRecipeCountByHashtagId(hashtagId);
                if (recipeCount == 0) {
                    mapper.deleteHashtagById(hashtagId);
                }
            }
        }
    }
    
    @Override
    public void increaseViewCountIfEligible(int recipeBoardId, String ipAddress) {
        RecipeBoardViewLogVO viewLogVO = new RecipeBoardViewLogVO();
        viewLogVO.setRecipeBoardId(recipeBoardId);
        viewLogVO.setIpAddress(ipAddress);

        int viewLogCount = mapper.isViewLogged(viewLogVO);
        System.out.println("View log count for IP " + ipAddress + " on recipeBoardId " + recipeBoardId + ": " + viewLogCount);

        if (viewLogCount == 0) {
            mapper.logView(viewLogVO);
            mapper.increaseViewCount(recipeBoardId);
            viewStatsMapper.incrementViewStats(recipeBoardId);
            System.out.println("View logged and count increased for IP " + ipAddress);
        } else {
            System.out.println("Duplicate view detected for IP " + ipAddress + " on recipeBoardId " + recipeBoardId);
        }
    }
    
}
