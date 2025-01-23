package com.dishcovery.project.service;

import java.util.List;

import com.dishcovery.project.domain.RecipeRankingVO;

public interface RecipeRankingService {

    /**
     * 랭킹 데이터를 가져오는 메서드
     * @param rankType 랭킹 유형 (DAILY, WEEKLY, MONTHLY)
     * @return 랭킹 데이터 리스트
     */
	List<RecipeRankingVO> getRankings(String rankType);
	
    /**
     * 특정 유형의 랭킹 데이터를 업데이트
     * @param type 랭킹 유형 (DAILY, WEEKLY, MONTHLY)
     */
    void updateRankings(String type);
}