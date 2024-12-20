package com.dishcovery.project.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dishcovery.project.domain.ReplyVO;
import com.dishcovery.project.persistence.RecipeBoardMapper;
import com.dishcovery.project.persistence.ReplyMapper;

import lombok.extern.log4j.Log4j;

@Service
@Log4j
public class ReplyServiceImple implements ReplyService{
	
	@Autowired
	private ReplyMapper replyMapper;
	
	@Autowired
	private RecipeBoardMapper recipeBoardMapper;
		
	@Transactional(value = "transactionManager")
	// transactionManager가 관리
	@Override
	public int createReply(ReplyVO replyVO) {
		// 댓글을 추가하면
		// Reply 테이블에 댓글이 등록
		log.info("CreateReply()");
		log.info("replyVO : " + replyVO.toString());
		// ReplyVO 객체 전체 정보 로깅
		int insertResult = replyMapper.insertReply(replyVO);
		log.info(insertResult + "행 댓글 추가");
//		int updateResult = recipeBoardMapper
//				.updateReplyCount(replyVO.getRecipeBoardId(), 1);
//		log.info(updateResult + "행 게시판 수정");
		return insertResult;
	}
	
	@Override
	public List<ReplyVO> getAllReply(int recipeBoardId) {
		log.info("getAllReply(), recipeBoardId: " + recipeBoardId);
		List<ReplyVO> list = replyMapper.selectListByRecipeBoardId(recipeBoardId);
	     log.info("list size : " + list.size());
	    return list;
	}
	
	@Override
	public int updateReply(int replyId, String replyContent) {
		log.info("replyContent");
		ReplyVO replyVO = new ReplyVO();
		replyVO.setReplyId(replyId);
		replyVO.setReplyContent(replyContent);
		return replyMapper.updateReply(replyVO);
	}
	
	@Transactional(value = "transactionManager")
	// transactionManager가 관리
	
	@Override
	public int deleteReply(int replyId, int recipeBoardId) {
		log.info("deleteReply()");
		int deleteResult = replyMapper.deleteReply(replyId);
		log.info(deleteResult + "행 댓글 삭제");
//		int updateResult = recipeBoardMapper
//				.updateReplyCount(recipeBoardId, -1);
//		log.info(updateResult + "행 게시판 수정");
		return 1;
	}
}
