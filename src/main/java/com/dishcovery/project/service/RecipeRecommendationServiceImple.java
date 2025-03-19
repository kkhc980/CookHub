package com.dishcovery.project.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.dishcovery.project.domain.CustomUser;
import com.dishcovery.project.persistence.RecipeBoardMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RecipeRecommendationServiceImple implements RecipeRecommendationService {

    private static final String RECOMMENDATION_API_URL = "http://127.0.0.1:5000/recommend?member_id={memberId}";

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private RecipeBoardMapper recipeBoardMapper;
    
    @Override
    public List<Map<String, Object>> getRecommendations(Authentication authentication) {
        int memberId = extractMemberId(authentication);

        // 회원인 경우
        if (memberId != -1) {
            List<Map<String, Object>> recommendations = getRecommendationsForMember(memberId);
            if (!recommendations.isEmpty()) {
                return recommendations;
            }
        }

        // 비회원이거나 회원인데 추천이 없는 경우
        return getRecommendationsForGuest();
    }

    private int extractMemberId(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof CustomUser) {
            CustomUser customUser = (CustomUser) authentication.getPrincipal();
            return customUser.getMemberVO().getMemberId();
        }
        return -1;
    }

    // 회원 추천 호출
    private List<Map<String, Object>> getRecommendationsForMember(int memberId) {
        try {
            ResponseEntity<List> response =
                    restTemplate.getForEntity(RECOMMENDATION_API_URL, List.class, memberId);
            log.info("✅ Flask API 응답: {}", response.getBody());
            return response.getBody();
        } catch (Exception e) {
            log.error("❌ Flask API 호출 실패: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    // 비회원 추천 호출
    private List<Map<String, Object>> getRecommendationsForGuest() {
        return recipeBoardMapper.getGuestRecommendations();
    }
}
