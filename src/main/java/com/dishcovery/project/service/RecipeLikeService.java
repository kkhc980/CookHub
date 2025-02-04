package com.dishcovery.project.service;

public interface RecipeLikeService {

    // 좋아요 토글
    boolean toggleLike(int recipeBoardId, int memberId);

    // 좋아요 개수 조회
    int getLikeCount(int recipeBoardId);
    
    // 특정 사용자의 좋아요 여부 확인
    boolean isLikedByUser(int recipeBoardId, int memberId);
}