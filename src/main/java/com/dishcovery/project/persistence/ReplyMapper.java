package com.dishcovery.project.persistence;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.dishcovery.project.domain.ReplyVO;
import com.dishcovery.project.util.Pagination;

@Mapper
public interface ReplyMapper {
	int insertReply(ReplyVO replyVO);
	List<ReplyVO> selectListByRecipeBoardId(
			@Param("recipeBoardId") int recipeBoardId,
            @Param("pagination") Pagination pagination);
	int getTotalReplyCount(@Param("recipeBoardId") int recipeBoardId);
	int updateReply(ReplyVO replyVO);
	int deleteReply(int replyId);
}
