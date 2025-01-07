package com.dishcovery.project.domain;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class RecipeDetailVO {
    private RecipeBoardVO recipeBoard;
    private String typeName;
    private String methodName;
    private String situationName;
    private List<IngredientsVO> ingredients;
    private List<HashtagsVO> hashtags; 
}
