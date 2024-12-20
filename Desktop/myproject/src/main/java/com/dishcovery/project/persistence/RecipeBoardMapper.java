package com.dishcovery.project.persistence;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.dishcovery.project.domain.IngredientsVO;
import com.dishcovery.project.domain.MethodsVO;
import com.dishcovery.project.domain.RecipeBoardVO;
import com.dishcovery.project.domain.SituationsVO;
import com.dishcovery.project.domain.TypesVO;

@Mapper
public interface RecipeBoardMapper {
int insert(RecipeBoardVO recipeBoardVO);
	
	List<RecipeBoardVO> selectList();
	
	int update (RecipeBoardVO recipeBoardVO);
	
	RecipeBoardVO selectOne(int recipeBoardId);

	int delete(int recipeBoardId);

	List<RecipeBoardVO> selectAll();

	int increaseViewCount(int recipeBoardId);

	List<RecipeBoardVO> selectByMemberId(String memberId);

	List<RecipeBoardVO> selectByIngredient(int ingredientId);

	List<RecipeBoardVO> selectByType(int typeId);

	List<RecipeBoardVO> selectByMethod(int methodId);

	List<RecipeBoardVO> selectBySituation(int situationId);

	List<TypesVO> getTypes();
	
	List<MethodsVO> getMethods();
	
	List<IngredientsVO> getIngredients();
	
	List<SituationsVO> getSituations();

	











}
