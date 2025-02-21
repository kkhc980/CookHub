package com.dishcovery.project.persistence;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.dishcovery.project.domain.RecipeRankingVO;

public interface RecipeViewStatsMapper {

    /**
     * 특정 레시피의 Daily, Weekly, Monthly View Count 증가
     * @param recipeBoardId 레시피 ID
     */
    void incrementViewStats(@Param("recipeBoardId") int recipeBoardId);

    /**
     * 특정 레시피의 초기 ViewStats 데이터 삽입
     * @param recipeBoardId 레시피 ID
     */
    void insertInitialViewStats(@Param("recipeBoardId") int recipeBoardId);

    /**
     * Daily 조회수 초기화
     */
    void resetDailyViewStats();

    /**
     * Weekly 조회수 초기화
     */
    void resetWeeklyViewStats();

    /**
     * Monthly 조회수 초기화
     */
    void resetMonthlyViewStats();
    
    List<RecipeRankingVO> getViewStatsByType(@Param("type") String type);
}