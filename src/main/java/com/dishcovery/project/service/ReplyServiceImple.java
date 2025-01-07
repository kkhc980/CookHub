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
	// transactionManager
	@Override
	public int createReply(ReplyVO replyVO) {
		log.info("CreateReply()");
		log.info("replyVO : " + replyVO.toString());
		int insertResult = replyMapper.insertReply(replyVO);
		log.info(insertResult + "�뻾 �뙎湲� 異붽�");
//		int updateResult = recipeBoardMapper
//				.updateReplyCount(replyVO.getRecipeBoardId(), 1);
//		log.info(updateResult + "�뻾 寃뚯떆�뙋 �닔�젙");
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
	// transactionManager
	
	@Override
	public int deleteReply(int replyId, int recipeBoardId) {
		log.info("deleteReply()");
		int deleteResult = replyMapper.deleteReply(replyId);
		log.info(deleteResult + "�뻾 �뙎湲� �궘�젣");
//		int updateResult = recipeBoardMapper
//				.updateReplyCount(recipeBoardId, -1);
//		log.info(updateResult + "�뻾 寃뚯떆�뙋 �닔�젙");
		return 1;
	}
}
