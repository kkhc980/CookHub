package com.dishcovery.project.domain;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class ReviewAttachDTO {
	private int attachId;
	private int recipeReviewId;
	private String attachPath;
	private String attachRealName;
	private String attachChgName;
	private String attachExtension;
	private Date attachDateCreated;
	
	
}