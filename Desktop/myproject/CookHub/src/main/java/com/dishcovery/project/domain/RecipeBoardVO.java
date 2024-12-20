package com.dishcovery.project.domain;

import java.util.Date;
import java.util.List;
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
    private List<IngredientsVO> ingredientList;  // 여러 개의 재료
    private List<SituationsVO> situationList;    // 여러 개의 상황
    private List<MethodsVO> methodList;          // 여러 개의 방법
    private List<TypesVO> typeList;              // 여러 개의 타입
    
    private String ingredientListStr; // 재료 리스트 문자열
    private String methodListStr;     // 방법 리스트 문자열
    private String situationListStr;  // 상황 리스트 문자열
    private String typeListStr;       // 타입 리스트 문자열
}