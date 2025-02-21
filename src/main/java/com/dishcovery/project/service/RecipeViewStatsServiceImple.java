package com.dishcovery.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dishcovery.project.persistence.RecipeViewStatsMapper;

@Service
public class RecipeViewStatsServiceImple implements RecipeViewStatsService {

    @Autowired
    private RecipeViewStatsMapper recipeViewStatsMapper;

    @Override
    public void incrementViewStats(int recipeBoardId) {
        recipeViewStatsMapper.incrementViewStats(recipeBoardId);
    }

    @Override
    public void insertInitialViewStats(int recipeBoardId) {
        recipeViewStatsMapper.insertInitialViewStats(recipeBoardId);
    }

    @Override
    public void resetDailyViews() {
        recipeViewStatsMapper.resetDailyViewStats();
    }

    @Override
    public void resetWeeklyViews() {
        recipeViewStatsMapper.resetWeeklyViewStats();
    }

    @Override
    public void resetMonthlyViews() {
        recipeViewStatsMapper.resetMonthlyViewStats();
    }
}