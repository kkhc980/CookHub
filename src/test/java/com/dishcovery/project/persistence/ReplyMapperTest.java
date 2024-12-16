package com.dishcovery.project.persistence;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.dishcovery.project.config.RootConfig;
import com.dishcovery.project.domain.ReplyVO;

import lombok.extern.log4j.Log4j;

@RunWith(SpringJUnit4ClassRunner.class) // 스프링 JUnit test연결
@ContextConfiguration(classes = { RootConfig.class}) // 설정 파일 연결
@Log4j
public class ReplyMapperTest {
	
	@Autowired
	private ReplyMapper replyMapper;
	
	@Test
	public void insertReply() {
	ReplyVO replyVO = new ReplyVO();
	replyVO.setBoardId(1);
	replyVO.setReplyContent("댓글 테스트");
	replyVO.setMemberId(2);
	int result = replyMapper.insert(replyVO);
	log.info(replyVO);
		
	}

}
