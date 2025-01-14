package com.dishcovery.project.service;

import com.dishcovery.project.domain.NoticeBoardVO;
import com.dishcovery.project.persistence.NoticeBoardMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoticeBoardServiceImple implements NoticeBoardService {

    // NoticeBoardMapper를 자동으로 주입
    @Autowired
    private NoticeBoardMapper noticeBoardMapper;

    // 모든 공지사항을 조회하는 메서드
    @Override
    public List<NoticeBoardVO> getAllNoticeBoards() {
        // Mapper의 selectAllNoticeBoards 메서드 호출
        return noticeBoardMapper.selectAllNoticeBoards();
    }

    // 특정 공지사항을 조회하는 메서드
    @Override
    public NoticeBoardVO getNoticeBoardById(int noticeBoardId) {
        // Mapper의 selectNoticeBoardById 메서드 호출
        return noticeBoardMapper.selectNoticeBoardById(noticeBoardId);
    }

    // 공지사항을 등록하는 메서드
    @Override
    public void addNoticeBoard(NoticeBoardVO noticeBoard) {
        // Mapper의 insertNoticeBoard 메서드 호출
        noticeBoardMapper.insertNoticeBoard(noticeBoard);
    }

    // 공지사항을 수정하는 메서드
    @Override
    public void updateNoticeBoard(NoticeBoardVO noticeBoard) {
        // Mapper의 updateNoticeBoard 메서드 호출
        noticeBoardMapper.updateNoticeBoard(noticeBoard);
    }

    // 공지사항을 삭제하는 메서드
    @Override
    public void deleteNoticeBoard(int noticeBoardId) {
        // Mapper의 deleteNoticeBoard 메서드 호출
        noticeBoardMapper.deleteNoticeBoard(noticeBoardId);
    }
}