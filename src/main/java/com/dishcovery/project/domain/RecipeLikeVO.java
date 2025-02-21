package com.dishcovery.project.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class RecipeLikeVO {
    private int likeId;          // 좋아요 ID
    private int recipeBoardId;   // 게시글 ID
    private int memberId;        // 회원 ID
    private String createdAt;    // 좋아요 누른 시간 (String으로 표현)
}
