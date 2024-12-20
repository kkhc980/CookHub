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
public class NoticeBoardVO {
int noticeBoardId;
String noticeBoardTitle;
String noticeBoardContent;
int memberId;
Date noticeBoardCreatedDate;
}
