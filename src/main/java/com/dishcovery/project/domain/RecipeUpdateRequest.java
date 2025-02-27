package com.dishcovery.project.domain;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class RecipeUpdateRequest {

    @NotNull(message = "레시피 ID는 필수입니다.")
    private int recipeBoardId;

    @NotBlank(message = "제목은 필수 입력 항목입니다.")
    @Size(min = 5, max = 100, message = "제목은 5자 이상 100자 이하로 입력해야 합니다.")
    private String recipeBoardTitle;

    private String recipeBoardContent;
    private int memberId; // CurrentUserId
    private int typeId;
    private int methodId;
    private int situationId;
    private MultipartFile thumbnail;
    private List<Integer> ingredientIds;
    private String hashtags;
    private String servings;
    private String time;
    private String difficulty;
    private String recipeTip;
    private List<Integer> deleteStepIds; 
    private List<String> stepDescriptions; // 각 스텝의 설명
    private List<MultipartFile> stepImages; // 각 스텝의 이미지
    private List<Integer> stepOrders; // 각 스텝의 순서 (선택 사항)
}
