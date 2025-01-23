package com.dishcovery.project.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dishcovery.project.domain.CustomUser;
import com.dishcovery.project.service.RecipeLikeService;

import lombok.extern.log4j.Log4j;

@RestController
@RequestMapping("/recipeboard")
@Log4j
public class RecipeLikeController {

    @Autowired
    RecipeLikeService recipeLikeService;

    // 좋아요 토글 처리
    @PostMapping("/{recipeBoardId}/like")
    public Map<String, Object> toggleLike(
            @PathVariable int recipeBoardId,
            @AuthenticationPrincipal CustomUser customUser) {

        if (customUser == null) {
            log.warn("CustomUser is null. Unauthorized access attempt to toggle like.");
            throw new RuntimeException("로그인이 필요합니다.");
        }

        int memberId = customUser.getMemberVO().getMemberId();
        log.info("Member ID: " + memberId + " toggling like for RecipeBoard ID: " + recipeBoardId);

        boolean liked = recipeLikeService.toggleLike(recipeBoardId, memberId);
        log.info("Like status changed: " + liked + " for RecipeBoard ID: " + recipeBoardId);

        int likeCount = recipeLikeService.getLikeCount(recipeBoardId);
        log.info("Updated like count for RecipeBoard ID: " + recipeBoardId + " -> " + likeCount);

        Map<String, Object> response = new HashMap<>();
        response.put("liked", liked);
        response.put("likeCount", likeCount);
        return response;
    }

    // 좋아요 개수 가져오기
    @GetMapping("/{recipeBoardId}/like-count")
    public Map<String, Object> getLikeCount(@PathVariable int recipeBoardId) {
        log.info("Fetching like count for RecipeBoard ID: " + recipeBoardId);

        int likeCount = recipeLikeService.getLikeCount(recipeBoardId);
        log.info("Fetched like count for RecipeBoard ID: " + recipeBoardId + " -> " + likeCount);

        Map<String, Object> response = new HashMap<>();
        response.put("likeCount", likeCount);
        return response;
    }
}
