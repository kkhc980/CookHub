package com.dishcovery.project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.dishcovery.project.domain.NoticeBoardVO;
import com.dishcovery.project.service.NoticeBoardService;

import lombok.extern.log4j.Log4j;

@Controller
@RequestMapping("/noticeboard")
@Log4j
public class NoticeBoardController {

    @Autowired
    private NoticeBoardService noticeBoardService;

    @GetMapping("/list")
    public String getAllNoticeBoards(Model model) {
        log.info("getAllNoticeBoards()");
        List<NoticeBoardVO> noticeBoardList = noticeBoardService.getAllNoticeBoards();
        model.addAttribute("noticeBoardList", noticeBoardList);
        model.addAttribute("pageContent", "noticeboard/list.jsp");
        return "layout";
    }

    @GetMapping("/detail/{noticeBoardId}")
    public String getNoticeBoardById(@PathVariable("noticeBoardId") int noticeBoardId, Model model) {
        NoticeBoardVO noticeBoard = noticeBoardService.getNoticeBoardById(noticeBoardId);
        model.addAttribute("NoticeBoardVO", noticeBoard);
        return "noticeboard/detail";
    }

    @GetMapping("/register")
    public String showAddNoticeForm() {
        return "noticeboard/register";
    }

    @PostMapping("/register")
    public String addNoticeBoard(@ModelAttribute NoticeBoardVO noticeBoard) {
        noticeBoardService.addNoticeBoard(noticeBoard);
        return "redirect:/noticeboard/list";
    }

    @GetMapping("/modify/{noticeBoardId}")
    public String showEditNoticeForm(@PathVariable("noticeBoardId") int noticeBoardId, Model model) {
        log.info("showEditNoticeForm() 호출, noticeBoardId: " + noticeBoardId);
        NoticeBoardVO noticeBoardVO = noticeBoardService.getNoticeBoardById(noticeBoardId);
        if (noticeBoardVO == null) {
            log.warn("noticeBoardVO가 null입니다. noticeBoardId: " + noticeBoardId);
            return "error";
        }
        log.info("showEditNoticeForm() 호출, noticeBoardVO: " + noticeBoardVO);
        model.addAttribute("noticeBoardVO", noticeBoardVO);
        return "noticeboard/modify";
    }

    @PostMapping("/modify/{noticeBoardId}")
    public String updateNoticeBoard(@PathVariable("noticeBoardId") int noticeBoardId, 
                                      @ModelAttribute NoticeBoardVO noticeBoard,
                                      Model model) {
        log.info("updateNoticeBoard() 호출, noticeBoardId: " + noticeBoardId);
        log.info("updateNoticeBoard() 호출, noticeBoard: " + noticeBoard);

        // 1. 기존 공지사항 정보 가져오기
        NoticeBoardVO existingNoticeBoard = noticeBoardService.getNoticeBoardById(noticeBoardId);
        if (existingNoticeBoard == null) {
            log.warn("기존 공지사항이 존재하지 않습니다. noticeBoardId: " + noticeBoardId);
            model.addAttribute("errorMessage", "존재하지 않는 공지사항입니다.");
            return "noticeboard/modify";
        }

      
        noticeBoard.setMemberId(existingNoticeBoard.getMemberId());
        log.info("기존 memberId 유지: " + existingNoticeBoard.getMemberId());

     
        try {
            noticeBoardService.updateNoticeBoard(noticeBoard);
            return "redirect:/noticeboard/detail/" + noticeBoard.getNoticeBoardId();
        } catch (Exception e) {
            log.error("공지사항 수정 중 오류 발생: " + e.getMessage());
            model.addAttribute("errorMessage", "공지사항 수정 중 오류가 발생했습니다.");
            return "noticeboard/modify";
        }
    }
    @PostMapping("/delete/{noticeBoardId}")
    public String deleteNoticeBoard(@PathVariable int noticeBoardId) {
        noticeBoardService.deleteNoticeBoard(noticeBoardId);
        return "redirect:/noticeboard/list";
    }
}