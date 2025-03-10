package com.dishcovery.project.service;

import com.dishcovery.project.domain.*;
import com.dishcovery.project.persistence.RecipeBoardMapper;
import com.dishcovery.project.persistence.RecipeRankingMapper;
import com.dishcovery.project.persistence.RecipeReviewMapper;
import com.dishcovery.project.persistence.RecipeViewStatsMapper;
import com.dishcovery.project.util.FileUploadUtil;
import com.dishcovery.project.util.PageMaker;
import com.dishcovery.project.util.Pagination;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Log4j
public class RecipeBoardServiceImple implements RecipeBoardService {

    @Autowired
    private RecipeBoardMapper mapper;

    @Autowired
    private RecipeViewStatsMapper viewStatsMapper;

    @Autowired
    private RecipeRankingMapper rankingMapper;
    
    @Autowired
    private RecipeReviewService recipeReviewService;
    
    @Autowired
    private RecipeReviewMapper recipeReviewMapper;  // 리뷰 매퍼 호출

    @Override
    public RecipeBoardVO getByRecipeBoardId(int recipeBoardId) {
        log.info("Fetching recipe board entry with ID: " + recipeBoardId);
        return mapper.getByRecipeBoardId(recipeBoardId);
    }

    @Override
    @Transactional
    public void createRecipe(RecipeRegisterRequest request) {
        // 1. RecipeRegisterRequest에서 데이터를 꺼내 RecipeBoardVO를 생성
        RecipeBoardVO recipeBoard = new RecipeBoardVO();
        recipeBoard.setRecipeBoardTitle(request.getRecipeBoardTitle());
        recipeBoard.setRecipeBoardContent(request.getRecipeBoardContent());
        int memberId = request.getMemberId();
        if (memberId <= 0) {
            log.error("Invalid memberId: " + memberId);
            throw new IllegalArgumentException("유효하지 않은 회원 ID입니다.");
        }
        recipeBoard.setMemberId(memberId);
        recipeBoard.setTypeId(request.getTypeId());
        recipeBoard.setMethodId(request.getMethodId());
        recipeBoard.setSituationId(request.getSituationId());
        recipeBoard.setServings(request.getServings());
        recipeBoard.setTime(request.getTime());
        recipeBoard.setDifficulty(request.getDifficulty());
        recipeBoard.setRecipeTip(request.getRecipeTip());

        try {
            // 썸네일 저장
            MultipartFile thumbnail = request.getThumbnail();
            if (thumbnail != null && !thumbnail.isEmpty()) {
                String thumbnailPath = saveThumbnail(thumbnail);
                recipeBoard.setThumbnailPath(thumbnailPath);
            } else {
                throw new IllegalArgumentException("Thumbnail is required for creating a recipe.");
            }

            // 게시글 ID 생성 및 저장
            log.info("Inserting recipe with memberId: {}" + recipeBoard.getMemberId());
            mapper.insertRecipeBoard(recipeBoard);
            log.info("Inserted recipe board with ID: " + recipeBoard.getRecipeBoardId());

            // RecipeViewStats 초기화 (insertInitialViewStats 호출)
            log.info("Inserting initial view stats for recipeBoardId: " + recipeBoard.getRecipeBoardId());
            viewStatsMapper.insertInitialViewStats(recipeBoard.getRecipeBoardId());

            // 재료 정보 추가
            List<Integer> ingredientIds = request.getIngredientIds();
            log.info("Adding ingredients to recipe with ID: " + recipeBoard.getRecipeBoardId());
            addIngredientsToRecipe(recipeBoard.getRecipeBoardId(), ingredientIds);

            // 해시태그 처리
            String hashtags = request.getHashtags();
            saveHashtagsForRecipe(recipeBoard.getRecipeBoardId(), hashtags);

             // 스텝 정보 추가
            List<String> stepDescriptions = request.getStepDescriptions();
            List<MultipartFile> stepImages = request.getStepImages();
            List<Integer> stepOrders = request.getStepOrders();

            if (stepDescriptions != null && !stepDescriptions.isEmpty()) {
                List<RecipeBoardStepVO> steps = new ArrayList<>();
                for (int i = 0; i < stepDescriptions.size(); i++) {
                    RecipeBoardStepVO step = new RecipeBoardStepVO();
                    step.setRecipeBoardId(recipeBoard.getRecipeBoardId());
                    step.setStepDescription(stepDescriptions.get(i));

                    // 스텝 순서 설정 (stepOrders가 제공되면 사용, 아니면 기본 순서 사용)
                    if (stepOrders != null && i < stepOrders.size()) {
                        step.setStepOrder(stepOrders.get(i));
                    } else {
                        step.setStepOrder(i + 1);
                    }

                    // 스텝 이미지 저장 및 URL 설정
                    if (stepImages != null && i < stepImages.size() && stepImages.get(i) != null && !stepImages.get(i).isEmpty()) {
                        String stepImageUrl = saveStepImage(stepImages.get(i)); // 스텝 이미지 저장 메서드
                        step.setStepImageUrl(stepImageUrl);
                    }

                    steps.add(step);
                }
                saveRecipeSteps(recipeBoard.getRecipeBoardId(), steps); // 스텝 정보 저장 메서드 호출
            }


        } catch (IOException e) {
            log.error("Failed to save thumbnail: " + e.getMessage(), e);
            throw new RuntimeException("Failed to create recipe due to thumbnail processing", e);
        } catch (Exception e) {
            log.error("createRecipe failed: " + e.getMessage(), e);
            throw new RuntimeException("Failed to create recipe", e);
        }
    }
    private String saveStepImage(MultipartFile multipartFile) {
		// TODO Auto-generated method stub
		return null;
	}

    @Override
    public RecipeBoardDTO getRecipeDetailById(int recipeBoardId) {
        log.info("Fetching recipe detail for ID: " + recipeBoardId);

        RecipeBoardVO recipeBoard = mapper.getByRecipeBoardId(recipeBoardId);
        if (recipeBoard == null)
            return null;

        RecipeBoardDTO detail = new RecipeBoardDTO();
        detail.setRecipeBoardId(recipeBoard.getRecipeBoardId());
        detail.setRecipeBoardTitle(recipeBoard.getRecipeBoardTitle());
        detail.setRecipeBoardContent(recipeBoard.getRecipeBoardContent());
        detail.setMemberId(recipeBoard.getMemberId());
        detail.setRecipeBoardCreatedDate(recipeBoard.getRecipeBoardCreatedDate());
        detail.setViewCount(recipeBoard.getViewCount());
        detail.setTypeName(mapper.getTypeName(recipeBoard.getTypeId()));
        detail.setMethodName(mapper.getMethodName(recipeBoard.getMethodId()));
        detail.setSituationName(mapper.getSituationName(recipeBoard.getSituationId()));
        detail.setAvgRating(recipeBoard.getAvgRating());
        detail.setRecipeReviewCount(recipeBoard.getRecipeReviewCount());
        detail.setThumbnailPath(recipeBoard.getThumbnailPath());
        detail.setServings(recipeBoard.getServings());
        detail.setTime(recipeBoard.getTime());
        detail.setDifficulty(recipeBoard.getDifficulty());
        detail.setRecipeTip(recipeBoard.getRecipeTip());

        // 재료 상세 정보 설정
        detail.setIngredientDetails(mapper.getIngredientsDetailByRecipeId(recipeBoardId));

        // 요리 순서 설정
        detail.setRecipeSteps(mapper.selectRecipeBoardStepsByBoardId(recipeBoardId));

        // 해시태그 설정 (List<HashtagsVO> 그대로 설정)
        detail.setHashtags(mapper.getHashtagsByRecipeId(recipeBoardId));

        detail.setIngredients(mapper.getIngredientsByRecipeId(recipeBoardId)); // 재료 목록 설정

        return detail;
    }
		
    @Override
    public RecipeDetailVO getRecipeDetailForUpdate(int recipeBoardId) {
        log.info("Fetching recipe detail for update with ID: " + recipeBoardId);

        RecipeBoardVO recipeBoard = mapper.getByRecipeBoardId(recipeBoardId);
        if (recipeBoard == null) {
            return null;
        }

        RecipeDetailVO recipeDetailVO = new RecipeDetailVO();
        recipeDetailVO.setRecipeBoard(recipeBoard);
        recipeDetailVO.setTypeName(mapper.getTypeName(recipeBoard.getTypeId()));
        recipeDetailVO.setMethodName(mapper.getMethodName(recipeBoard.getMethodId()));
        recipeDetailVO.setSituationName(mapper.getSituationName(recipeBoard.getSituationId()));
        recipeDetailVO.setIngredients(mapper.getIngredientsByRecipeId(recipeBoardId));
        recipeDetailVO.setHashtags(mapper.getHashtagsByRecipeId(recipeBoardId));
        recipeDetailVO.setIngredientDetails(mapper.getIngredientsDetailByRecipeId(recipeBoardId));
        recipeDetailVO.setRecipeSteps(mapper.selectRecipeBoardStepsByBoardId(recipeBoardId));

        // 재료 ID 문자열 생성 (update.jsp에서 사용하기 위해)
        List<Integer> ingredientIdsList = mapper.getIngredientsByRecipeId(recipeBoardId).stream()
                .map(IngredientsVO::getIngredientId)
                .collect(Collectors.toList());

        if (ingredientIdsList != null && !ingredientIdsList.isEmpty()) {
            String ingredientIdsString = ingredientIdsList.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
            recipeBoard.setIngredientIdsString(ingredientIdsString); // RecipeBoardVO에 설정
        }
        
        return recipeDetailVO;
    }

	 @Override
	    @Transactional
	    public void updateRecipe(RecipeUpdateRequest request) {
	        int id = request.getRecipeBoardId();
	        RecipeBoardVO existingRecipe = getByRecipeBoardId(id);
	        if (existingRecipe == null) {
	            throw new IllegalArgumentException("Recipe not found with id: " + id);
	        }

	        // 1. RecipeUpdateRequest에서 데이터를 꺼내 RecipeBoardVO를 업데이트
	        RecipeBoardVO recipeBoard = new RecipeBoardVO();
	        recipeBoard.setRecipeBoardId(request.getRecipeBoardId());
	        recipeBoard.setRecipeBoardTitle(request.getRecipeBoardTitle());
	        recipeBoard.setRecipeBoardContent(request.getRecipeBoardContent());
	        recipeBoard.setMemberId(request.getMemberId());
	        recipeBoard.setTypeId(request.getTypeId());
	        recipeBoard.setMethodId(request.getMethodId());
	        recipeBoard.setSituationId(request.getSituationId());
	        recipeBoard.setServings(request.getServings());
	        recipeBoard.setTime(request.getTime());
	        recipeBoard.setDifficulty(request.getDifficulty());
	        recipeBoard.setRecipeTip(request.getRecipeTip());
	        try {

	            MultipartFile thumbnail = request.getThumbnail();
	            String thumbnailPath = null;
	            if (thumbnail != null && !thumbnail.isEmpty()) {
	                thumbnailPath = saveThumbnail(thumbnail);
	            } else {
	                thumbnailPath = existingRecipe.getThumbnailPath();
	            }
	            recipeBoard.setThumbnailPath(thumbnailPath);
	            // 2. RecipeBoardVO를 사용하여 데이터베이스에 레시피를 업데이트
	            mapper.updateRecipeBoard(recipeBoard);

	            // 3. 나머지 로직 (해시태그, 재료 등)
	            updateHashtags(id, request.getHashtags());
	            mapper.deleteRecipeIngredientsByRecipeId(id);
	            addIngredientsToRecipe(id, request.getIngredientIds());

	               // 스텝 정보 업데이트
	            List<String> stepDescriptions = request.getStepDescriptions();
	            List<MultipartFile> stepImages = request.getStepImages();
	            List<Integer> stepOrders = request.getStepOrders();
	            List<Integer> deleteStepIds = request.getDeleteStepIds(); // 삭제할 스텝 ID 목록

	            // 삭제할 스텝 삭제
	            if (deleteStepIds != null && !deleteStepIds.isEmpty()) {
	                for (int stepId : deleteStepIds) {
	                    mapper.deleteRecipeBoardStepByStepId(stepId);
	                }
	            }

	            if (stepDescriptions != null && !stepDescriptions.isEmpty()) {
	                List<RecipeBoardStepVO> steps = new ArrayList<>();
	                for (int i = 0; i < stepDescriptions.size(); i++) {
	                    RecipeBoardStepVO step = new RecipeBoardStepVO();
	                    step.setRecipeBoardId(recipeBoard.getRecipeBoardId());
	                    step.setStepDescription(stepDescriptions.get(i));

	                    // 스텝 순서 설정 (stepOrders가 제공되면 사용, 아니면 기본 순서 사용)
	                    if (stepOrders != null && i < stepOrders.size()) {
	                        step.setStepOrder(stepOrders.get(i));
	                    } else {
	                        step.setStepOrder(i + 1);
	                    }

	                    // 스텝 이미지 저장 및 URL 설정
	                    if (stepImages != null && i < stepImages.size() && stepImages.get(i) != null && !stepImages.get(i).isEmpty()) {
	                        String stepImageUrl = saveStepImage(stepImages.get(i)); // 스텝 이미지 저장 메서드
	                        step.setStepImageUrl(stepImageUrl);
	                    }

	                    steps.add(step);
	                }
	                saveRecipeSteps(recipeBoard.getRecipeBoardId(), steps); // 스텝 정보 저장 메서드 호출
	            }

	        }  catch (IOException e) {
	            log.error("Failed to save thumbnail or step image: " + e.getMessage(), e);
	            throw new RuntimeException("Failed to update recipe due to file processing", e);
	        } catch (Exception e) {
	            log.error("updateRecipe failed: " + e.getMessage(), e);
	            throw new RuntimeException("Failed to update recipe", e);
	        }
	    }


    private void updateHashtags(int recipeBoardId, String hashtags) {
        // 기존 해시태그 가져오기
        List<String> existingHashtags = mapper.getHashtagNamesByRecipeId(recipeBoardId);

        // 새롭게 전달된 해시태그 리스트 생성
        List<String> newHashtags = hashtags != null ? Arrays.asList(hashtags.split(",")) : List.of();

        List<String> hashtagsToRemove = existingHashtags.stream().filter(tag -> !newHashtags.contains(tag))
                .collect(Collectors.toList());

        List<String> hashtagsToAdd = newHashtags.stream().filter(tag -> !existingHashtags.contains(tag))
                .collect(Collectors.toList());

        // 해시태그 추가 및 삭제
        addHashtagsToRecipe(recipeBoardId, hashtagsToAdd);
        removeHashtagsFromRecipe(recipeBoardId, hashtagsToRemove);
    }

    @Override
    @Transactional
    public void saveRecipeSteps(int recipeBoardId, List<RecipeBoardStepVO> steps) {
        try {
            if (steps == null || steps.isEmpty()) {
                return;
            }

            for (RecipeBoardStepVO step : steps) {
                // stepId가 null인지 체크
                if (step.getStepId() == null) {
                    // 새로운 스텝 처리
                    step.setRecipeBoardId(recipeBoardId);
                    mapper.insertRecipeBoardStep(step);
                    log.info("insertRecipeBoardStep called with: " + step);
                } else {
                    // 기존 스텝 수정 처리
                    mapper.updateRecipeBoardStep(step);
                    log.info("updateRecipeBoardStep called with: " + step);
                }
            }
        } catch (Exception e) {
            log.error("saveRecipeSteps failed " + e.getMessage(), e);
            throw new RuntimeException("Failed to save recipe steps", e);
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
    public List<IngredientsVO> getIngredientsByRecipeId(int recipeBoardId) {
        log.info("Fetching ingredients for recipe ID: " + recipeBoardId);
        return mapper.getAllIngredients();
    }

    @Override
    public List<RecipeIngredientsDetailVO> getRecipeIngredientsDetailsByRecipeId(int recipeBoardId) {
        log.info("Fetching recipe ingredient details for ID: " + recipeBoardId);
        return mapper.getIngredientsDetailByRecipeId(recipeBoardId);
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
        return mapper.getIngredientsByRecipeId(recipeBoardId).stream().map(IngredientsVO::getIngredientId)
                .collect(Collectors.toSet());
    }

    @Override
    public List<RecipeBoardStepVO> getRecipeBoardStepsByBoardId(int recipeBoardId) {
        log.info("Fetching recipe steps for recipe ID: " + recipeBoardId);
        return mapper.selectRecipeBoardStepsByBoardId(recipeBoardId);
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
        if (pagination.getSort() == null || pagination.getSort().isEmpty()) {
            pagination.setSort("latest");
        }

        return pagination;
    }

    @Override
    public Map<String, Object> getRecipeBoardListWithFilters(Pagination pagination) {
        pagination = preprocessPagination(pagination);
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
    public Optional<ImageData> getThumbnailByRecipeBoardId(int recipeBoardId) {
        try {
            RecipeBoardVO recipeBoard = getByRecipeBoardId(recipeBoardId);

            if (recipeBoard == null || recipeBoard.getThumbnailPath() == null) {
                return Optional.empty();
            }

            File file = new File("C:/uploads/" + recipeBoard.getThumbnailPath());
            if (!file.exists()) {
                return Optional.empty();
            }

            byte[] imageData = Files.readAllBytes(file.toPath());
            String contentType = Files.probeContentType(file.toPath()); // 이미지 MIME 타입 감지

            return Optional.of(new ImageData(imageData, contentType));
        } catch (IOException e) {
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
        if (recipeBoardId == 0) {
            throw new IllegalArgumentException("Invalid recipeBoardId: 0");
        }
        // recipeBoardId 유효성 검사
        RecipeBoardVO recipeBoard = mapper.getByRecipeBoardId(recipeBoardId); // 예시: mapper를 사용하여 조회
        if (recipeBoard == null) {
            throw new IllegalArgumentException("Invalid recipeBoardId: " + recipeBoardId);
        }

        if (ingredientIds != null && !ingredientIds.isEmpty()) {
            ingredientIds.forEach(ingredientId -> {
                RecipeIngredientsVO recipeIngredient = new RecipeIngredientsVO();
                recipeIngredient.setRecipeBoardId(recipeBoardId);
                recipeIngredient.setIngredientId(ingredientId);
                mapper.insertRecipeIngredient(recipeIngredient);
            });
        }
    }

    private void addIngredientDetailsToRecipe(int recipeBoardId, List<RecipeIngredientsDetailVO> ingredientDetails) {
        log.info("addIngredientDetailsToRecipe called with recipeBoardId: " + recipeBoardId + ", ingredientDetails: "
                + ingredientDetails);
        if (ingredientDetails == null || ingredientDetails.isEmpty()) {
            log.info("ingredientDetails is null or empty. skipping...");
            return;
        }
        ingredientDetails.forEach(ingredientDetail -> {
            ingredientDetail.setRecipeBoardId(recipeBoardId);
            // ingredientDetailId가 이미 설정되어 있다면, 해당 값 유지, 그렇지 않으면 null로 유지 (시퀀스에서 자동 생성)
            mapper.insertRecipeIngredientsDetail(ingredientDetail);
            log.info("insertRecipeIngredientsDetail called with detail : " + ingredientDetail);
        });
    }

    @Override
    public List<String> getHashtagNamesByRecipeBoardId(int recipeBoardId) {
        // 해시태그 VO 리스트를 가져오고, 이름 리스트로 변환
        return mapper.getHashtagsByRecipeId(recipeBoardId).stream().map(HashtagsVO::getHashtagName)
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
        System.out.println(
                "View log count for IP " + ipAddress + " on recipeBoardId " + recipeBoardId + ": " + viewLogCount);

        if (viewLogCount == 0) {
            mapper.logView(viewLogVO);
            mapper.increaseViewCount(recipeBoardId);
            viewStatsMapper.incrementViewStats(recipeBoardId);
            System.out.println("View logged and count increased for IP " + ipAddress);
        } else {
            System.out.println("Duplicate view detected for IP " + ipAddress + " on recipeBoardId " + recipeBoardId);
        }
    }

    @Override
    public RecipeBoardVO findRecipeById(int id) {
        log.info("Fetching recipe board entry with ID: " + id);
        return mapper.getByRecipeBoardId(id);
    }

    @Override
    public Map<String, Object> findAllRecipes(Pagination pagination, Integer typeId, Integer situationId,
                                               Integer methodId, String ingredientIds, String hashtag) {
        pagination.setTypeId(typeId);
        pagination.setSituationId(situationId);
        pagination.setMethodId(methodId);
        pagination.setIngredientIdsFromString(ingredientIds);
        pagination.setHashtag(hashtag);
        pagination = preprocessPagination(pagination);

        Map<String, Object> result = getRecipeBoardListWithFilters(pagination);

        return result;
    }
    
    @Override
    @Transactional
    public void updateAverageRating(int recipeBoardId) {
    	log.info("🚀 [디버깅] updateAverageRating 메서드 실행됨! recipeBoardId: " + recipeBoardId); // ← 실행 여부 확인
    	
    	try {
    	// 1. 리뷰 별점 평균 구하기
    	Double avgRating = recipeReviewMapper.getReviewRating(recipeBoardId);
    	
    	// avgRating이 null인 경우 0으로 처리
    	if (avgRating == null) {
    		avgRating = 0.0;
    	}
    	    	
    	// 2. AVG_RATING 업데이트
        mapper.updateAvgRating(recipeBoardId, avgRating);
        
    } catch (Exception e) {
        log.error("평점 업데이트 중 오류 발생", e);
    }
    	
    }
    

    @Override
    public int getTotalRecipeBoardCount() {
        return mapper.getTotalRecipeBoardCount();
    }

}