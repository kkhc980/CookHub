package com.dishcovery.project.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dishcovery.project.domain.IngredientsVO;
import com.dishcovery.project.domain.MethodsVO;
import com.dishcovery.project.domain.RecipeBoardVO;
import com.dishcovery.project.domain.SituationsVO;
import com.dishcovery.project.domain.TypesVO;
import com.dishcovery.project.persistence.RecipeBoardMapper;

import lombok.extern.log4j.Log4j;

@Service
@Log4j
public class RecipeBoardServiceImple  implements RecipeBoardService{

	  @Autowired
	    private RecipeBoardMapper recipeBoardMapper;

	    @Override
	    public int createRecipeBoard(RecipeBoardVO recipeBoardVO) {
	        log.info("insertRecipeBoard()");
	        return recipeBoardMapper.insert(recipeBoardVO);
	    }

	    @Override
	    public RecipeBoardVO getRecipeBoard(int recipeBoardId) {
	        log.info("getRecipeBoard()");
	        return recipeBoardMapper.selectOne(recipeBoardId);
	    }

	    @Override
	    public List<RecipeBoardVO> getBoardList() {
	        log.info("getBoardList()");
	        return recipeBoardMapper.selectAll();
	    }

	    @Override
	    public int updateRecipeBoard(RecipeBoardVO recipeBoard) {
	        log.info("updateRecipeBoard()");
	        return recipeBoardMapper.update(recipeBoard);
	    }

	    @Override
	    public int deleteRecipeBoard(int recipeBoardId) {
	        log.info("deleteRecipeBoard() ");
	        return recipeBoardMapper.delete(recipeBoardId);
	    }

	    @Override
	    public int increaseViewCount(int recipeBoardId) {
	        log.info("increaseViewCount() ");
	        return recipeBoardMapper.increaseViewCount(recipeBoardId);
	    }

	    @Override
	    public List<RecipeBoardVO> getRecipeBoardsByMemberId(String memberId) {
	        log.info("getRecipeBoardsByMemberId() ");
	        return recipeBoardMapper.selectByMemberId(memberId);
	    }

	    @Override
	    public List<RecipeBoardVO> getRecipeBoardsByType(int typeId) {
	        log.info("getRecipeBoardsByType() ");
	        return recipeBoardMapper.selectByType(typeId);
	    }

	    @Override
	    public List<RecipeBoardVO> getRecipeBoardsByIngredient(int ingredientId) {
	        log.info("getRecipeBoardsByIngredient() ");
	        return recipeBoardMapper.selectByIngredient(ingredientId);
	    }

	     @Override
	    public List<RecipeBoardVO> getRecipeBoardsByMethod(int methodId) {
	         log.info("getRecipeBoardsByMethod()");
	        return recipeBoardMapper.selectByMethod(methodId);
	    }


	    @Override
	    public List<RecipeBoardVO> getRecipeBoardsBySituation(int situationId) {
	        log.info("getRecipeBoardsBySituation() ");
	        return recipeBoardMapper.selectBySituation(situationId);
	    }

		@Override
		public List<TypesVO> getTypes() {
			log.info("getTypes()");
			return recipeBoardMapper.getTypes();
		}

		@Override
		public List<IngredientsVO> getIngredients() {
			log.info("getAllIngredients()");
			return recipeBoardMapper.getIngredients();
		}

		@Override
		public List<MethodsVO> getMethods() {
			log.info("getMethod()");
			return recipeBoardMapper.getMethods();
		}

		@Override
		public List<SituationsVO> getSituations() {
			log.info("getSituation()");
			return recipeBoardMapper.getSituations();
		}

		@Override
		public RecipeBoardVO getRecipeBoardsById(int recipeBoardId) {
			log.info("getRecipeBoardsById()");
			increaseViewCount(recipeBoardId); // 조회수 증가
			return recipeBoardMapper.selectOne(recipeBoardId);
		}
}
