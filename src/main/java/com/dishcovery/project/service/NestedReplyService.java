package com.dishcovery.project.service;

import java.util.List;

import com.dishcovery.project.domain.NestedReplyVO;

public interface NestedReplyService {
	int createNestedReply(NestedReplyVO nestedReplyVO);
	List<NestedReplyVO> getAllNestedReply(int replyId);
	int updateNestedReply(int nestedReplyId, String nestedReplyContent);
	int deleteNestedReply(int nestedReplyId, int replyId);
}
