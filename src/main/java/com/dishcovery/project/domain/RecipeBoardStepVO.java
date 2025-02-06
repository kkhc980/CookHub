package com.dishcovery.project.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
@Getter
@Setter
@ToString
@NoArgsConstructor
public class RecipeBoardStepVO {
    Integer stepId;  
    int recipeBoardId;
    int stepOrder;
    String stepDescription;
    String stepImageUrl;
}