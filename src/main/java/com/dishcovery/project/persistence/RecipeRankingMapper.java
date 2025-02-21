package com.dishcovery.project.persistence;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.dishcovery.project.domain.RecipeRankingVO;

public interface RecipeRankingMapper {

    /**
     * 특정 유형의 랭킹 데이터를 가져오는 메서드
     * @param rankType 랭킹 유형 (DAILY, WEEKLY, MONTHLY)
     * @return 랭킹 데이터 리스트
     */
	List<RecipeRankingVO> getRankings(@Param("rankType") String rankType);
	
    /**
     * 랭킹 데이터를 삽입
     * @param rankings 랭킹 데이터 리스트
     */
	void insertRankings(@Param("rank") RecipeRankingVO ranking);

    /**
     * 특정 유형의 기존 랭킹 데이터를 삭제
     * @param rankType 랭킹 유형 (DAILY, WEEKLY, MONTHLY)
     */
    void deleteRankingsByType(String rankType);
    
    void reorderRankPositions();
}
