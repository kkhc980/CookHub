package com.dishcovery.project.persistence;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.dishcovery.project.domain.HashtagsVO;
import com.dishcovery.project.domain.IngredientsVO;
import com.dishcovery.project.domain.MethodsVO;
import com.dishcovery.project.domain.RecipeBoardVO;
import com.dishcovery.project.domain.RecipeBoardViewLogVO;
import com.dishcovery.project.domain.RecipeHashtagsVO;
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
    List<RecipeBoardVO> getRecipeBoardListWithPaging(Pagination pagination); // 필터 및 페이징 적용 Recipe 조회
    int getTotalCountWithFilters(Pagination pagination); // 필터 적용된 Recipe 수 조회
    int getTotalCount(); // 전체 Recipe 수 조회

    // Sequence
    int getNextRecipeBoardId(); // SEQUENCE에서 다음 Recipe ID 가져오기
    int getNextHashtagId();
    
    // ViewLog
    int isViewLogged(RecipeBoardViewLogVO viewLogVO); // 조회 기록 여부 확인
    void logView(RecipeBoardViewLogVO viewLogVO); // 조회 기록 추가
    void increaseViewCount(int recipeBoardId); // 조회수 증가 
    int deleteOldViewLogs(); // 이전 날짜 조회 기록 삭제
    void deleteRecipeViewLogsByRecipeId(int recipeBoardId); // 조회 기록 삭제
    
    // 저장된 썸네일 조회
    List<String> getAllThumbnailPaths();
}