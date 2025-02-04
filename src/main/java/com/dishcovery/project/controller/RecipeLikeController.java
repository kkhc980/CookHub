package com.dishcovery.project.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
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
    public ResponseEntity<Map<String, Object>> toggleLike(
            @PathVariable int recipeBoardId,
            @AuthenticationPrincipal CustomUser customUser) {

        Map<String, Object> response = new HashMap<>();

        // 비로그인 상태 처리
        if (customUser == null) {
            log.warn("Unauthorized access attempt to toggle like for RecipeBoard ID: " + recipeBoardId);
            response.put("liked", false);
            response.put("message", "로그인이 필요한 서비스입니다.");
            return ResponseEntity.ok(response);
        }

        // 로그인된 사용자 ID 가져오기
        int memberId = customUser.getMemberVO().getMemberId();
        log.info("Member ID: " + memberId + " toggling like for RecipeBoard ID: " + recipeBoardId);

        // 좋아요 상태 변경 처리
        boolean liked = recipeLikeService.toggleLike(recipeBoardId, memberId);
        log.info("Like status changed: " + liked + " for RecipeBoard ID: " + recipeBoardId);

        // 업데이트된 좋아요 개수 가져오기
        int likeCount = recipeLikeService.getLikeCount(recipeBoardId);
        log.info("Updated like count for RecipeBoard ID: " + recipeBoardId + " -> " + likeCount);

        // 응답 데이터 설정
        response.put("liked", liked);
        response.put("likeCount", likeCount);
        response.put("message", liked ? "좋아요가 설정되었습니다." : "좋아요가 취소되었습니다.");

        // 성공 응답 반환
        return ResponseEntity.ok(response);
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
    
    @GetMapping("/{recipeBoardId}/like-status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getLikeStatus(
            @PathVariable int recipeBoardId,
            @AuthenticationPrincipal CustomUser customUser) {

        Map<String, Object> response = new HashMap<>();

        if (customUser == null) {
            log.info("Unauthenticated user accessing like-status for RecipeBoard ID: " + recipeBoardId);
            return ResponseEntity.ok(response); // 200 OK 반환
        }

        int memberId = customUser.getMemberVO().getMemberId();
        boolean liked = recipeLikeService.isLikedByUser(recipeBoardId, memberId);
        log.info("Member ID: " + memberId + " like status for RecipeBoard ID: " + recipeBoardId + " -> " + liked);

        response.put("liked", liked);
        return ResponseEntity.ok(response);
    }

}
