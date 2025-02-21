package com.dishcovery.project.service;

public interface RecipeViewStatsService {

    /**
     * 특정 레시피의 Daily, Weekly, Monthly 조회수 증가
     * @param recipeBoardId 레시피 ID
     */
    void incrementViewStats(int recipeBoardId);

    /**
     * 특정 레시피의 ViewStats 초기 데이터 삽입
     * @param recipeBoardId 레시피 ID
     */
    void insertInitialViewStats(int recipeBoardId);

    /**
     * Daily 조회수 초기화
     */
    void resetDailyViews();

    /**
     * Weekly 조회수 초기화
     */
    void resetWeeklyViews();

    /**
     * Monthly 조회수 초기화
     */
    void resetMonthlyViews();
}