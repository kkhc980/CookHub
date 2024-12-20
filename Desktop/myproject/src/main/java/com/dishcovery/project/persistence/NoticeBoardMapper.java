package com.dishcovery.project.persistence;

import java.util.List;

import com.dishcovery.project.domain.NoticeBoardVO;

public interface NoticeBoardMapper {

    // 모든 공지사항을 조회하는 메서드
    List<NoticeBoardVO> selectAllNoticeBoards();

    // 특정 공지사항을 조회하는 메서드
    NoticeBoardVO selectNoticeBoardById(int noticeBoardId);

    // 새로운 공지사항을 등록하는 메서드
    void insertNoticeBoard(NoticeBoardVO noticeBoard);

    // 기존 공지사항을 수정하는 메서드
    void updateNoticeBoard(NoticeBoardVO noticeBoard);

    // 공지사항을 삭제하는 메서드
    void deleteNoticeBoard(int noticeBoardId);
}
