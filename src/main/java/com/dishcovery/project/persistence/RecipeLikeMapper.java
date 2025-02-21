package com.dishcovery.project.persistence;

import org.apache.ibatis.annotations.Param;

import com.dishcovery.project.domain.RecipeLikeVO;

public interface RecipeLikeMapper {

    // 좋아요 추가
    void insertLike(RecipeLikeVO likeVO);

    // 좋아요 삭제
    void deleteLike(RecipeLikeVO likeVO);

    // 좋아요 여부 확인
    int isLikedByUser(@Param("recipeBoardId") int recipeBoardId, @Param("memberId") int memberId);

    // 좋아요 개수 조회
    int getLikeCount(int recipeBoardId);
}
