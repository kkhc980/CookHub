package com.dishcovery.project.persistence;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.dishcovery.project.domain.NoticeBoardVO;

@Mapper
public interface NoticeBoardMapper {

    // 모든 공지사항 조회
    List<NoticeBoardVO> selectAllNoticeBoards();

    // 특정 공지사항 조회
    NoticeBoardVO selectNoticeBoardById(int noticeBoardId);

    // 공지사항 등록
    void insertNoticeBoard(NoticeBoardVO noticeBoard);

    // 공지사항 수정
    void updateNoticeBoard(NoticeBoardVO noticeBoard);

    // 공지사항 삭제
    void deleteNoticeBoard(int noticeBoardId);

    // 최신 공지사항 5개 조회
    List<NoticeBoardVO> getLatestNotices(@Param("limit") int limit);
}
