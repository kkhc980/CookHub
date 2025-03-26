package com.dishcovery.project.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dishcovery.project.domain.NestedReplyVO;
import com.dishcovery.project.persistence.NestedReplyMapper;
import com.dishcovery.project.persistence.ReplyMapper;

import lombok.extern.log4j.Log4j;

@Service
@Log4j
public class NestedReplyServiceImple implements NestedReplyService {
	
	@Autowired
	private NestedReplyMapper nestedReplyMapper;
	
	@Autowired
	private ReplyMapper replyMapper;
	
	@Transactional(value = "transactionManager")
	
	@Override
	public int createNestedReply(NestedReplyVO nestedReplyVO) {
		log.info("CreateNestedReply()");
		log.info("nestedReplyVO : " + nestedReplyVO.toString());
		int insertResult = nestedReplyMapper.insertNestedReply(nestedReplyVO);
		log.info(insertResult + "덧글 입력 완료");
		
		return insertResult;
	}
	
	@Override
	public List<NestedReplyVO> getAllNestedReply(int replyId) {
		log.info("getAllNestedReply(), replyId: " + replyId);
		List<NestedReplyVO> list = nestedReplyMapper.selectListByReplyId(replyId);
		log.info("list size : " + list.size());
		return list;
	}
	
	@Override
	public int updateNestedReply(int nestedReplyId, String nestedReplyContent) {
		log.info("nestedReplyContent");
		NestedReplyVO nestedReplyVO = new NestedReplyVO();
		nestedReplyVO.setNestedReplyId(nestedReplyId);
		nestedReplyVO.setNestedReplyContent(nestedReplyContent);
		return nestedReplyMapper.updateNestedReply(nestedReplyVO);
	}
	
	@Override
	public int deleteNestedReply(int nestedReplyId, int replyId) {
		log.info("deleteNestedReply()");
		int deleteResult = nestedReplyMapper.deleteNestedReply(nestedReplyId);
		log.info(deleteResult + "덧글 삭제 완료");
		
		return 1;
	}
	
	@Override
	public NestedReplyVO getNestedReplyById(int nestedReplyId) {
	    return nestedReplyMapper.selectNestedReplyById(nestedReplyId);
	}
	
}
