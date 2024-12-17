package com.dishcovery.project.domain;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RecipeBoardVO {
    private int recipeBoardId;           // RECIPE_BOARD_ID
    private String recipeBoardTitle;     // RECIPE_BOARD_TITLE
    private String recipeBoardContent;   // RECIPE_BOARD_CONTENT
    private int memberId;                // MEMBER_ID
    private Date recipeBoardCreatedDate; // RECIPE_BOARD_CREATED_DATE
    private int viewCount;               // VIEW_COUNT
    private int typeId;                  // TYPE_ID
    private int ingredientId;            // INGREDIENT_ID
    private int methodId;                // METHOD_ID
    private int situationId;             // SITUATION_ID
    private Double avgRating;            // AVG_RATING
}
