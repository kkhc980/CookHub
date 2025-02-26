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
public class NestedReplyVO {
	private int nestedReplyId;
	private int memberId;
	private int replyId;
	private String nestedReplyContent;
	private Date nestedReplyDateCreated;
}
