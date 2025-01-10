package com.dishcovery.project.service;

import com.dishcovery.project.domain.NoticeBoardVO;

import java.util.List;

public interface NoticeBoardService {

    // 모든 공지사항을 조회하는 메서드
    List<NoticeBoardVO> getAllNoticeBoards();

    // 특정 공지사항을 조회하는 메서드
    NoticeBoardVO getNoticeBoardById(int noticeBoardId);

    // 공지사항을 등록하는 메서드
    void addNoticeBoard(NoticeBoardVO noticeBoard);

    // 공지사항을 수정하는 메서드
    void updateNoticeBoard(NoticeBoardVO noticeBoard);

    // 공지사항을 삭제하는 메서드
    void deleteNoticeBoard(int noticeBoardId);
}