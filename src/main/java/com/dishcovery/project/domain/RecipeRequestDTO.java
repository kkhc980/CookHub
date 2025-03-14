package com.dishcovery.project.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class RecipeRequestDTO {
    private RecipeBoardVO recipeBoard;
    private List<Integer> ingredientIds;
    private String hashtags;
    private List<String> stepDescriptions;
    private List<Integer> stepOrders;
    private String servings;
    private String time;
    private String difficulty;
    private List<String> ingredientNames;
    private List<String> ingredientAmounts;
    private List<String> ingredientUnits;
    private List<String> ingredientNotes;
}
