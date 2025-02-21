package com.dishcovery.project.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class RecipeIngredientsDetailVO {
	int ingredientDetailId;
	int recipeBoardId;
	String ingredientName;
	String ingredientAmount;
	String ingredientUnit;
	String ingredientNote;
}
