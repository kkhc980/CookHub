package com.dishcovery.project.service;

import java.util.List;

import com.dishcovery.project.domain.ReplyVO;

public interface ReplyService {
	int createReply(ReplyVO replyVO);
	List<ReplyVO> getAllReply(int board);
	int updateReply(int replyId, String replyContent);
	int deleteReply(int replyId, int boardId);
}
