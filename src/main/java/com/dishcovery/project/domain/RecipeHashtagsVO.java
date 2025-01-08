package com.dishcovery.project.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NoArgsConstructor;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class RecipeHashtagsVO {
    private int recipeBoardId; // 레시피 게시글 ID (FK)
    private int hashtagId;     // 해시태그 ID (FK)
}