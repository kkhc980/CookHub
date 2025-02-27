package com.dishcovery.project.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@ToString
public class RecipeRegisterRequest {
    private String recipeBoardTitle;
    private String recipeBoardContent;
    private int memberId;
    private int typeId;
    private int methodId;
    private int situationId;
    private String thumbnailPath;
    private List<Integer> ingredientIds;
    private String hashtags;
    private MultipartFile thumbnail;
    private String servings;
    private String time;
    private String difficulty;
    private String recipeTip;
    private List<String> stepDescriptions;
    private List<MultipartFile> stepImages;
  private List<Integer> stepOrders;
  
    private List<String> ingredientName;
    private List<String> ingredientAmount;
    private List<String> ingredientUnit;
    private List<String> ingredientNote;
}