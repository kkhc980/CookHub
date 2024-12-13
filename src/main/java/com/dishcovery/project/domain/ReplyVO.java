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
public class ReplyVO {
	private int replyId;
	private int boardId;
	private String memberId;
	private String replyContent;
	private Date replyDateCreated;
	
}
