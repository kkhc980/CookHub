package com.dishcovery.project.domain;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class RecipeIngredientDetailsList {
    private List<RecipeIngredientsDetailVO> ingredientDetails;

        public List<RecipeIngredientsDetailVO> getIngredientDetails() {
        return ingredientDetails;
    }
}