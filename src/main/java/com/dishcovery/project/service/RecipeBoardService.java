package com.dishcovery.project.service;

import java.util.List;

import com.dishcovery.project.domain.IngredientsVO;
import com.dishcovery.project.domain.MethodsVO;
import com.dishcovery.project.domain.RecipeBoardVO;
import com.dishcovery.project.domain.SituationsVO;
import com.dishcovery.project.domain.TypesVO;

public interface RecipeBoardService {
	 // 레시피 게시글 등록
    int createRecipeBoard(RecipeBoardVO recipeBoardVO);

    // 레시피 게시글 조회
    RecipeBoardVO getRecipeBoard(int recipeBoardId);

    // 레시피 게시글 목록 조회
    List<RecipeBoardVO> getBoardList();

    // 레시피 게시글 수정
    int updateRecipeBoard(RecipeBoardVO recipeBoard);

    // 레시피 게시글 삭제
    int deleteRecipeBoard(int recipeBoardId);

    // 조회수 증가
    int increaseViewCount(int recipeBoardId);

    List<RecipeBoardVO> selectAll();

    // 특정 사용자의 레시피 게시글 목록 조회
    List<RecipeBoardVO> getRecipeBoardsByMemberId(String memberId);

    // 카테고리별 레시피 조회
    List<RecipeBoardVO> getRecipeBoardsByType(int typeId);

    List<RecipeBoardVO> getRecipeBoardsByIngredient(int ingredientId);

    List<RecipeBoardVO> getRecipeBoardsByMethod(int methodId);

    List<RecipeBoardVO> getRecipeBoardsBySituation(int situationId);

    List<TypesVO> getTypes();

    List<IngredientsVO> getIngredients();

    List<MethodsVO> getMethods();

    List<SituationsVO> getSituations();

	RecipeBoardVO getRecipeBoardsById(int recipeBoardId);
}