package com.dishcovery.project.persistence;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.dishcovery.project.config.RootConfig;
import com.dishcovery.project.domain.ReplyVO;

import lombok.extern.log4j.Log4j;

@RunWith(SpringJUnit4ClassRunner.class) // 스프링 JUnit test 연결
@ContextConfiguration(classes = { RootConfig.class }) // 설정 파일 연결
@Log4j
public class ReplyMapperTest {
	
	@Autowired
	private ReplyMapper replyMapper;
	
	@Test
//	public void test() {
//	   insertReply();
//	   }
//
//	private void insertReply() {
//		ReplyVO replyVO = new ReplyVO();
//		replyVO.setRecipeBoardId(2);
//		replyVO.setReplyContent("댓글 테스트");
//		replyVO.setMemberId(1);
//		int result = replyMapper.insertReply(replyVO);
	
//		log.info(replyVO);
//	} // end insertReply()
	
//	public void test() {
//		updateReply();
//	}
//	
//	private void updateReply() {
//		ReplyVO replyVO	= new ReplyVO();
//		replyVO.setReplyId(3);
//		replyVO.setReplyContent("댓글 수정 테스트");
//		int result = replyMapper.updateReply(replyVO);
	
//		log.info(replyVO);
//	} // end updateReply()
		
	public void test() {
		deleteReply();
	}
	
	private void deleteReply() {
		ReplyVO replyVO = new ReplyVO();
		replyVO.setReplyContent("댓글 삭제 테스트");
		int result = replyMapper.deleteReply(3);
		log.info(replyVO);
	} // end deleteReply()
	
}

