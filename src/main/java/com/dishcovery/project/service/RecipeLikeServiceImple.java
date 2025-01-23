package com.dishcovery.project.service;

import com.dishcovery.project.domain.RecipeLikeVO;
import com.dishcovery.project.persistence.RecipeLikeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecipeLikeServiceImple implements RecipeLikeService {

    private final RecipeLikeMapper recipeLikeMapper;

    @Override
    public boolean toggleLike(int recipeBoardId, int memberId) {
        RecipeLikeVO likeVO = new RecipeLikeVO();
        likeVO.setRecipeBoardId(recipeBoardId);
        likeVO.setMemberId(memberId);

        int isLiked = recipeLikeMapper.isLiked(likeVO);

        if (isLiked > 0) {
            recipeLikeMapper.deleteLike(likeVO);
            return false; // 좋아요 해제
        } else {
            recipeLikeMapper.insertLike(likeVO);
            return true; // 좋아요 설정
        }
    }

    @Override
    public int getLikeCount(int recipeBoardId) {
        return recipeLikeMapper.getLikeCount(recipeBoardId);
    }
}
