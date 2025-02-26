package com.dishcovery.project.service;

import java.util.List;

import com.dishcovery.project.domain.ReplyVO;
import com.dishcovery.project.util.Pagination;

public interface ReplyService {
	List<ReplyVO> getAllReply(int recipeBoardId, Pagination pagination); // Pagination 추가
	int getTotalReplyCount(int recipeBoardId); // 총 댓글 수 조회 메서드 추가
	int createReply(ReplyVO replyVO);
	int updateReply(int replyId, String replyContent);
	int deleteReply(int replyId, int recipeBoardId);
	
}
