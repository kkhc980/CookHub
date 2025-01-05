package com.dishcovery.project.persistence;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.dishcovery.project.config.RootConfig;
import com.dishcovery.project.domain.ReplyVO;

import lombok.extern.log4j.Log4j;

@RunWith(SpringJUnit4ClassRunner.class) 
@ContextConfiguration(classes = { RootConfig.class }) 
@Log4j
public class ReplyMapperTest {
	
	@Autowired
	private ReplyMapper replyMapper;
	
	@Test
	public void test() {
	   insertReply();
	   }

	private void insertReply() {
		ReplyVO replyVO = new ReplyVO();
		replyVO.setRecipeBoardId(1);
		replyVO.setReplyContent("댓글 생성 test");
		replyVO.setMemberId(1);
		int result = replyMapper.insertReply(replyVO);
	
		log.info(replyVO);
	} // end insertReply()
	
//	public void test() {
//		updateReply();
//	}
//	
//	private void updateReply() 
//		ReplyVO replyVO	= new ReplyVO();
//		replyVO.setReplyId(16);
//		replyVO.setReplyContent("댓글 update test");
//		int result = replyMapper.updateReply(replyVO);
//	
//		log.info(replyVO);
//	} // end updateReply()
		
//	public void test() {
//		deleteReply();
//	}
//	
//	private void deleteReply() {
//		ReplyVO replyVO = new ReplyVO();
//		replyVO.setReplyContent("댓글 삭제");
//		int result = replyMapper.deleteReply(3);
//		log.info(replyVO);
//	} // end deleteReply()
	
}

