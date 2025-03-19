package com.dishcovery.project.service;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;

public interface RecipeRecommendationService {
	List<Map<String, Object>> getRecommendations(Authentication authentication);
}
