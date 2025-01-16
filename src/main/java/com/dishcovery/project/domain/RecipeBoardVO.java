package com.dishcovery.project.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class RecipeBoardVO {

    private int recipeBoardId;
    private String recipeBoardTitle;
    private String recipeBoardContent;
    private int memberId;
    private Date recipeBoardCreatedDate;
    private int viewCount;
    private int typeId;
    private int methodId;
    private int situationId;
    private double avgRating;
    private int replyCount;
    private int recipeReviewCount;
    private String thumbnailPath;
    private List<Integer> ingredientIds;
    private String hashtags;
    private MultipartFile thumbnail;
}
