package com.dishcovery.project.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dishcovery.project.domain.ReplyVO;
import com.dishcovery.project.persistence.RecipeBoardMapper;
import com.dishcovery.project.persistence.ReplyMapper;
import com.dishcovery.project.util.Pagination;

import lombok.extern.log4j.Log4j;

@Service
@Log4j
public class ReplyServiceImple implements ReplyService{
	
	@Autowired
	private ReplyMapper replyMapper;
	
	@Autowired
	private RecipeBoardMapper recipeBoardMapper;
		
	@Transactional(value = "transactionManager")
	// transactionManager
	@Override
	public int createReply(ReplyVO replyVO) {
		log.info("CreateReply()");
		log.info("replyVO : " + replyVO.toString());
		int insertResult = replyMapper.insertReply(replyVO);
		log.info(insertResult);
//		int updateResult = recipeBoardMapper
//				.updateReplyCount(replyVO.getRecipeBoardId(), 1);
//		log.info(updateResult);
		return insertResult;
	}
	
	@Override
	public List<ReplyVO> getAllReply(int recipeBoardId, Pagination pagination) {
		log.info("getAllReply(), recipeBoardId: " + recipeBoardId);
		List<ReplyVO> list = replyMapper.selectListByRecipeBoardId(recipeBoardId, pagination);
	     log.info("list size : " + list.size());
	    return list;
	}
	
	 @Override
	    public int getTotalReplyCount(int recipeBoardId) {
	        log.info("getTotalReplyCount(), recipeBoardId: " + recipeBoardId);
	        return replyMapper.getTotalReplyCount(recipeBoardId);
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
	// transactionManager
	
	@Override
	public int deleteReply(int replyId, int recipeBoardId) {
		log.info("deleteReply()");
		int deleteResult = replyMapper.deleteReply(replyId);
		log.info(deleteResult + "�뻾 �뙎湲� �궘�젣");
//		int updateResult = recipeBoardMapper
//				.updateReplyCount(recipeBoardId, -1);
//		log.info(updateResult);
		return 1;
	}
}
