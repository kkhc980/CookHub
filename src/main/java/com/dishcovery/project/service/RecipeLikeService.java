package com.dishcovery.project.service;

public interface RecipeLikeService {

    // 좋아요 토글
    boolean toggleLike(int recipeBoardId, int memberId);

    // 좋아요 개수 조회
    int getLikeCount(int recipeBoardId);
}