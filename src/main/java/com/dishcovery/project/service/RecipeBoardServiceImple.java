package com.dishcovery.project.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.dishcovery.project.domain.HashtagsVO;
import com.dishcovery.project.domain.ImageData;
import com.dishcovery.project.domain.IngredientsVO;
import com.dishcovery.project.domain.MethodsVO;
import com.dishcovery.project.domain.RecipeBoardStepVO;
import com.dishcovery.project.domain.RecipeBoardVO;
import com.dishcovery.project.domain.RecipeBoardViewLogVO;
import com.dishcovery.project.domain.RecipeDetailVO;
import com.dishcovery.project.domain.RecipeHashtagsVO;
import com.dishcovery.project.domain.RecipeIngredientsDetailVO;
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
	public void createRecipe(RecipeBoardVO recipeBoard, List<Integer> ingredientIds, String hashtags,
			MultipartFile thumbnail, List<RecipeBoardStepVO> steps,
			List<RecipeIngredientsDetailVO> ingredientsDetails) {
		if (thumbnail == null || thumbnail.isEmpty()) {
			throw new IllegalArgumentException("Thumbnail is required for creating a recipe.");
		}

		try {
			// 썸네일 저장
			String thumbnailPath = saveThumbnail(thumbnail);
			recipeBoard.setThumbnailPath(thumbnailPath);

			// 게시글 ID 생성 및 저장

			log.info("Inserting recipe with memberId: {}" + recipeBoard.getMemberId());
			log.info(recipeBoard.getRecipeBoardId());
			mapper.insertRecipeBoard(recipeBoard);
			log.info("Inserted recipe board with ID: " + recipeBoard.getRecipeBoardId());

			// RecipeViewStats 초기화 (insertInitialViewStats 호출)
			log.info("Inserting initial view stats for recipeBoardId: " + recipeBoard.getRecipeBoardId());
			viewStatsMapper.insertInitialViewStats(recipeBoard.getRecipeBoardId());

			// 재료 정보 추가
			log.info("Adding ingredients to recipe with ID: " + recipeBoard.getRecipeBoardId());
			addIngredientsToRecipe(recipeBoard.getRecipeBoardId(), ingredientIds);
			// 재료 상세 정보 추가
			if (ingredientsDetails != null && !ingredientsDetails.isEmpty()) {
				addIngredientDetailsToRecipe(recipeBoard.getRecipeBoardId(), ingredientsDetails);
			}
			// 해시태그 처리
			saveHashtagsForRecipe(recipeBoard.getRecipeBoardId(), hashtags);

			// 스텝 정보 추가
			if (steps != null && !steps.isEmpty()) {
				saveRecipeSteps(recipeBoard.getRecipeBoardId(), steps);
			} else {
				log.info("steps is empty or null");
			}
		} catch (Exception e) {
			log.error("createRecipe failed " + e.getMessage(), e);
			throw new RuntimeException("Failed to create recipe with thumbnail and hashtags", e);
		}
	}

	@Override
	@Transactional
	public void updateRecipe(int id, RecipeBoardVO recipe, List<Integer> ingredientIds, String hashtags,
			MultipartFile thumbnail, List<RecipeBoardStepVO> steps, List<Integer> deleteStepIds,
			List<RecipeIngredientsDetailVO> ingredientDetails) {

		try {
			RecipeBoardVO existingRecipe = getByRecipeBoardId(id);
			if (existingRecipe == null) {
				throw new IllegalArgumentException("Recipe not found with id: " + id);
			}

			if (existingRecipe.getRecipeBoardId() == 0) {
				throw new IllegalArgumentException("RecipeBoardId is not set properly after getByRecipeBoardId.");
			}

			String thumbnailPath = null;
			if (thumbnail != null && !thumbnail.isEmpty()) {

				thumbnailPath = saveThumbnail(thumbnail);
			} else {

				thumbnailPath = existingRecipe.getThumbnailPath();
			}
			recipe.setThumbnailPath(thumbnailPath);
			recipe.setRecipeBoardId(id);
			mapper.updateRecipeBoard(recipe);
			   mapper.deleteRecipeIngredientsByRecipeId(id);
		        addIngredientsToRecipe(id, ingredientIds);
			mapper.deleteRecipeIngredientsByRecipeId(id);
			addIngredientsToRecipe(id, ingredientIds);
			mapper.deleteRecipeIngredientsDetailsByRecipeId(id);
			if (ingredientDetails != null && !ingredientDetails.isEmpty()) {
				addIngredientDetailsToRecipe(id, ingredientDetails);
				  log.info("Updated ingredientDetails: " + ingredientDetails);
			}

			updateHashtags(id, hashtags);

			if (deleteStepIds != null && !deleteStepIds.isEmpty()) {
				for (int stepId : deleteStepIds) {
					mapper.deleteRecipeBoardStepByStepId(stepId);
				}
			}
			if (steps != null && !steps.isEmpty()) {
				saveRecipeSteps(id, steps);
			}

		} catch (IllegalArgumentException e) {
			throw e;
		} catch (Exception e) {
			// 그 외 예외 발생 시
			throw new RuntimeException("Failed to update recipe with hashtags", e);
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
	public RecipeDetailVO getRecipeDetailById(int recipeBoardId) {
		log.info("Fetching recipe detail for ID: " + recipeBoardId);

		RecipeBoardVO recipeBoard = mapper.getByRecipeBoardId(recipeBoardId);
		if (recipeBoard == null)
			return null;

		RecipeDetailVO detail = new RecipeDetailVO();
		detail.setRecipeBoard(recipeBoard);
		detail.setTypeName(mapper.getTypeName(recipeBoard.getTypeId()));
		detail.setMethodName(mapper.getMethodName(recipeBoard.getMethodId()));
		detail.setSituationName(mapper.getSituationName(recipeBoard.getSituationId()));
		detail.setIngredients(mapper.getIngredientsByRecipeId(recipeBoardId));
		detail.setHashtags(mapper.getHashtagsByRecipeId(recipeBoardId));
		detail.setRecipeSteps(mapper.selectRecipeBoardStepsByBoardId(recipeBoardId));
		return detail;
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

}
