package com.dishcovery.project.domain;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class RecipeRankingVO {
    private int rankId;          // 랭킹 ID (Primary Key)
    private int rankPosition;    // 순위
    private int recipeBoardId;   // 레시피 게시판 ID
    private int viewCount;       // 조회수
    private String rankType;     // 랭킹 유형 (DAILY, WEEKLY, MONTHLY)
    private Date updatedDate;    // 랭킹 생성 날짜
}