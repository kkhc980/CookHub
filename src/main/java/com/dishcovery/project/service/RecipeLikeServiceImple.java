package com.dishcovery.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dishcovery.project.domain.RecipeLikeVO;
import com.dishcovery.project.persistence.RecipeLikeMapper;

@Service
public class RecipeLikeServiceImple implements RecipeLikeService {

    @Autowired
    private RecipeLikeMapper recipeLikeMapper;

    @Override
    public boolean toggleLike(int recipeBoardId, int memberId) {
        RecipeLikeVO likeVO = new RecipeLikeVO();
        likeVO.setRecipeBoardId(recipeBoardId);
        likeVO.setMemberId(memberId);

        if (isLikedByUser(recipeBoardId, memberId)) {
            // 좋아요 취소
            recipeLikeMapper.deleteLike(likeVO);
            return false;
        } else {
            // 좋아요 추가
            recipeLikeMapper.insertLike(likeVO);
            return true;
        }
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
