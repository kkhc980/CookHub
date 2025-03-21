package com.dishcovery.project.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.dishcovery.project.domain.NoticeBoardVO;
import com.dishcovery.project.persistence.NoticeBoardMapper;

import lombok.extern.log4j.Log4j;

@Service
@Log4j
public class NoticeBoardServiceImple implements NoticeBoardService {

    @Autowired
    private NoticeBoardMapper noticeBoardMapper;

    @Override
    public List<NoticeBoardVO> getAllNoticeBoards() {
        log.info("getAllNoticeBoards() 호출");
        try {
            List<NoticeBoardVO> noticeBoardList = noticeBoardMapper.selectAllNoticeBoards();
            log.info("getAllNoticeBoards() 종료");
            return noticeBoardList;
        } catch (Exception e) {
            log.error("공지사항 조회 중 오류 발생", e);
            throw new RuntimeException("공지사항 조회 중 오류 발생", e);
        }
    }

    @Override
    public NoticeBoardVO getNoticeBoardById(int noticeBoardId) {
        log.info("getNoticeBoardById() 호출, noticeBoardId: " + noticeBoardId);
        try {
            NoticeBoardVO noticeBoard = noticeBoardMapper.selectNoticeBoardById(noticeBoardId);
            log.info("getNoticeBoardById() 종료, noticeBoard: " + noticeBoard);
            return noticeBoard;
        } catch (Exception e) {
            log.error("공지사항 조회 중 오류 발생, noticeBoardId: " + noticeBoardId, e);
            throw new RuntimeException("공지사항 조회 중 오류 발생, noticeBoardId: " + noticeBoardId, e);
        }
    }

    @Override
    @Transactional
    public void addNoticeBoard(NoticeBoardVO noticeBoard) {
        log.info("addNoticeBoard() 호출, noticeBoard: " + noticeBoard);
        try {
            noticeBoard.setNoticeBoardCreatedDate(new Date());
            noticeBoardMapper.insertNoticeBoard(noticeBoard);
            log.info("addNoticeBoard() 종료, noticeBoardId: " + noticeBoard.getNoticeBoardId());
        } catch (Exception e) {
            log.error("공지사항 등록 중 오류 발생, noticeBoard: " + noticeBoard, e);
            throw new RuntimeException("공지사항 등록 중 오류 발생", e);
        }
    }

    @Override
    @Transactional
    public void updateNoticeBoard(NoticeBoardVO noticeBoard) {
        log.info("updateNoticeBoard() 호출, noticeBoard: " + noticeBoard);
        try {
            noticeBoardMapper.updateNoticeBoard(noticeBoard);
            log.info("updateNoticeBoard() 종료, noticeBoardId: " + noticeBoard.getNoticeBoardId());
        } catch (Exception e) {
            log.error("공지사항 수정 중 오류 발생, noticeBoard: " + noticeBoard, e);
             TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new RuntimeException("공지사항 수정 중 오류 발생: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void deleteNoticeBoard(int noticeBoardId) {
        log.info("deleteNoticeBoard() 호출, noticeBoardId: " + noticeBoardId);
        try {
            noticeBoardMapper.deleteNoticeBoard(noticeBoardId);
            log.info("deleteNoticeBoard() 종료, noticeBoardId: " + noticeBoardId);
        } catch (Exception e) {
            log.error("공지사항 삭제 중 오류 발생, noticeBoardId: " + noticeBoardId, e);
            throw new RuntimeException("공지사항 삭제 중 오류 발생", e);
        }
    }
    
    @Override
    public List<NoticeBoardVO> getLatestNotices(int limit) {
        return noticeBoardMapper.getLatestNotices(limit);
    }
}