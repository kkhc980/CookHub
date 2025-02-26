package com.dishcovery.project.persistence;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.dishcovery.project.domain.NestedReplyVO;

@Mapper
public interface NestedReplyMapper {
	int insertNestedReply(NestedReplyVO nestedReplyVO);
	List<NestedReplyVO> selectListByReplyId(int replyId);
	int updateNestedReply(NestedReplyVO nestedReplyVO);
	int deleteNestedReply(int nestedReplyId);
}
