package com.dishcovery.project.persistence;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.dishcovery.project.domain.ReplyVO;

@Mapper
public interface ReplyMapper {
	int insertReply(ReplyVO replyVO);
	List<ReplyVO> selectListByRecipeBoardId(int recipeBoardId);
	int updateReply(ReplyVO replyVO);
	int deleteReply(int replyId);
}
