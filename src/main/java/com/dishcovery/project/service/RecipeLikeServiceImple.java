package com.dishcovery.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dishcovery.project.domain.RecipeLikeVO;
import com.dishcovery.project.persistence.RecipeBoardMapper;
import com.dishcovery.project.persistence.RecipeLikeMapper;

@Service
public class RecipeLikeServiceImple implements RecipeLikeService {

    @Autowired
    private RecipeLikeMapper recipeLikeMapper;

    @Autowired
    private RecipeBoardMapper recipeBoardMapper;
    @Override
    public boolean toggleLike(int recipeBoardId, int memberId) {
        RecipeLikeVO likeVO = new RecipeLikeVO();
        likeVO.setRecipeBoardId(recipeBoardId);
        likeVO.setMemberId(memberId);

        boolean liked;
        if (isLikedByUser(recipeBoardId, memberId)) {
            // 좋아요 취소
            recipeLikeMapper.deleteLike(likeVO);
            liked = false;
        } else {
            // 좋아요 추가
            recipeLikeMapper.insertLike(likeVO);
            liked = true;
        }
        
        // 최신 좋아요 개수 가져오기
        int likeCount = recipeLikeMapper.getLikeCount(recipeBoardId);

        // recipeboard 테이블의 like_count 업데이트
        recipeBoardMapper.updateLikeCount(recipeBoardId, likeCount);

        return liked;
    }

    @Override
    public int getLikeCount(int recipeBoardId) {
        return recipeLikeMapper.getLikeCount(recipeBoardId);
    }

    @Override
    public boolean isLikedByUser(int recipeBoardId, int memberId) {
        return recipeLikeMapper.isLikedByUser(recipeBoardId, memberId) > 0;
    }
}
