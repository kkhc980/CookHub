package com.dishcovery.project.persistence;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.dishcovery.project.domain.HashtagsVO;
import com.dishcovery.project.domain.IngredientsVO;
import com.dishcovery.project.domain.MemberVO;
import com.dishcovery.project.domain.MethodsVO;
import com.dishcovery.project.domain.RecipeBoardStepVO;
import com.dishcovery.project.domain.RecipeBoardVO;
import com.dishcovery.project.domain.RecipeBoardViewLogVO;
import com.dishcovery.project.domain.RecipeHashtagsVO;
import com.dishcovery.project.domain.RecipeIngredientsDetailVO;
import com.dishcovery.project.domain.RecipeIngredientsVO;
import com.dishcovery.project.domain.SituationsVO;
import com.dishcovery.project.domain.TypesVO;
import com.dishcovery.project.util.Pagination;

@Mapper
public interface RecipeBoardMapper {

    // RecipeBoard CRUD
    RecipeBoardVO getByRecipeBoardId(int recipeBoardId); // 특정 Recipe 조회
    void insertRecipeBoard(RecipeBoardVO recipeBoard); // Recipe 등록
    void updateRecipeBoard(RecipeBoardVO recipeBoard); // Recipe 업데이트
    void deleteRecipeBoard(int recipeBoardId); // Recipe 삭제
    
    // Recipe Ingredient Detail
    List<RecipeIngredientsDetailVO> getIngredientsDetailByRecipeId(int recipeBoardId); // 특정 레시피의 재료 상세 정보 조회
    void insertRecipeIngredientsDetail(RecipeIngredientsDetailVO recipeIngredientDetail); // 레시피 재료 상세 정보 추가
    // 특정 레시피의 모든 재료 상세 정보 삭제
    void deleteRecipeIngredientsDetailsByRecipeId(int id);

    // Recipe Ingredients
    List<IngredientsVO> getAllIngredients(); // 모든 재료 조회
    List<IngredientsVO> getIngredientsByRecipeId(int recipeBoardId); // 특정 Recipe의 재료 조회
    void insertRecipeIngredient(RecipeIngredientsVO recipeIngredients); // Recipe에 재료 추가
    void deleteRecipeIngredientsByRecipeId(int recipeBoardId); // 특정 Recipe의 모든 재료 삭제

    // Hashtags
    List<HashtagsVO> getHashtagsByRecipeId(int recipeBoardId); // 특정 Recipe의 해시태그 조회
    HashtagsVO getHashtagByName(String hashtagName); // 해시태그 이름으로 조회
    void insertHashtag(HashtagsVO hashtag); // 새 해시태그 추가
    void insertRecipeHashtag(RecipeHashtagsVO recipeHashtag); // Recipe-Hashtag 연결 추가
    void deleteRecipeHashtagsByRecipeId(int recipeBoardId); // 특정 Recipe의 모든 해시태그 연결 삭제
    int getRecipeCountByHashtagId(int hashtagId); // 해시태그가 연결된 다른 게시글 수 확인
    void deleteHashtagById(int hashtagId);  // 해시태그 삭제
    void deleteRecipeHashtagsByRecipeIdAndHashtagId(@Param("recipeBoardId") int recipeBoardId, @Param("hashtagId") int hashtagId);
    List<String> getHashtagNamesByRecipeId(int recipeBoardId);
    
    // Types, Methods, Situations
    List<TypesVO> getAllTypes(); // 모든 Type 조회
    List<MethodsVO> getAllMethods(); // 모든 Method 조회
    List<SituationsVO> getAllSituations(); // 모든 Situation 조회
    String getTypeName(int typeId); // Type 이름 조회
    String getMethodName(int methodId); // Method 이름 조회
    String getSituationName(int situationId); // Situation 이름 조회

    // Pagination & Filtering
    List<RecipeBoardVO> getRecipeBoardListWithPaging(@Param("pagination") Pagination pagination); // 필터 및 페이징 적용 Recipe 조회
    int getTotalCountWithFilters(Pagination pagination); // 필터 적용된 Recipe 수 조회
    int getTotalCount(); // 전체 Recipe 수 조회

    // Sequence
    int getNextRecipeBoardId(); // SEQUENCE에서 다음 Recipe ID 가져오기
    int getNextHashtagId();
    
    // Recipe Steps
    void insertRecipeBoardStep(RecipeBoardStepVO recipeBoardStep); // 레시피 스텝 추가
    List<RecipeBoardStepVO> selectRecipeBoardStepsByBoardId(int recipeBoardId); // 특정 게시글의 레시피 스텝 목록 조회
    RecipeBoardStepVO selectRecipeBoardStepByStepId(int stepId); // 특정 레시피 스텝 조회
    void updateRecipeBoardStep(RecipeBoardStepVO recipeBoardStep); // 레시피 스텝 수정
    void deleteRecipeBoardStepByStepId(int stepId); // 특정 스텝 삭제
    void deleteRecipeBoardStepsByBoardId(int recipeBoardId); // 특정 게시글의 모든 스텝 삭제
	List<RecipeBoardVO> selectAllRecipes(Pagination pagination);
	
    // ViewLog
    int isViewLogged(RecipeBoardViewLogVO viewLogVO); // 조회 기록 여부 확인
    void logView(RecipeBoardViewLogVO viewLogVO); // 조회 기록 추가
    void increaseViewCount(int recipeBoardId); // 조회수 증가 
    int deleteOldViewLogs(); // 이전 날짜 조회 기록 삭제
    void deleteRecipeViewLogsByRecipeId(int recipeBoardId); // 조회 기록 삭제
    
    // 저장된 썸네일 조회
    List<String> getAllThumbnailPaths();
    
    // 좋아요 개수 업데이트
    void updateLikeCount(@Param("recipeBoardId") int recipeBoardId, @Param("likeCount") int likeCount);

	MemberVO getMemberByUsername(String username);
	

	// reviewCount 업데이트
	void updateRecipeReviewCount(@Param("recipeBoardId") int recipeBoardId);
	
	// avgRating update
	void updateAvgRating(@Param("recipeBoardId") int recipeBoardId, @Param("avgRating") Double avgRating);


	int getTotalRecipeBoardCount();
	
	List<RecipeBoardVO> getTopPosts();
	
	List<Map<String, Object>> getGuestRecommendations();
	
	void batchInsertIngredients(List<RecipeIngredientsVO> ingredients);
	void batchInsertIngredientDetails(List<RecipeIngredientsDetailVO> details);
	void batchInsertSteps(List<RecipeBoardStepVO> steps);
	
	
	List<HashtagsVO> selectHashtagsByNames(List<String> tagNames);
	void batchInsertHashtags(List<HashtagsVO> hashtags);

    // 🔸 레시피-해시태그 다건 매핑 insert
    void batchInsertRecipeHashtags(List<RecipeHashtagsVO> recipeHashtags);
}