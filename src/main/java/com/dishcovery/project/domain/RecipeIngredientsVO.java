package com.dishcovery.project.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class RecipeIngredientsVO {
	
    private int recipeBoardId;
    private int ingredientId;
    
    public RecipeIngredientsVO(int recipeBoardId, int ingredientId) {
        this.recipeBoardId = recipeBoardId;
        this.ingredientId = ingredientId;
    }
}