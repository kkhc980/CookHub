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

    // 모든 공지사항을 조회하는 메서드
    @GetMapping("/list")
    public String getAllNoticeBoards(Model model) {
    	log.info("getAllNoticeBoards()");
        List<NoticeBoardVO> noticeBoardList = noticeBoardService.getAllNoticeBoards();
        model.addAttribute("noticeBoardList", noticeBoardList);
        return "noticeboard/list";  
    }

    // 특정 공지사항을 조회하는 메서드
    @GetMapping("/view")
    public String getNoticeBoardById(@PathVariable("noticeBoardId") int noticeBoardId, Model model) {
        NoticeBoardVO noticeBoard = noticeBoardService.getNoticeBoardById(noticeBoardId);
        model.addAttribute("noticeBoard", noticeBoard);
        return "noticeboard/view";  
    }

    // 공지사항 등록 폼을 보여주는 메서드
    @GetMapping("/register")
    public String showAddNoticeForm() {
        return "noticeboard/add";  
    }

    // 새로운 공지사항을 등록하는 메서드
    @PostMapping("/register")
    public String addNoticeBoard(@ModelAttribute NoticeBoardVO noticeBoard) {
        noticeBoardService.addNoticeBoard(noticeBoard);
        return "redirect:/noticeboard/list";  
    }

    // 공지사항 수정 폼을 보여주는 메서드
    @GetMapping("/modify")
    public String showEditNoticeForm(@PathVariable("noticeBoardId") int noticeBoardId, Model model) {
        NoticeBoardVO noticeBoard = noticeBoardService.getNoticeBoardById(noticeBoardId);
        model.addAttribute("noticeBoard", noticeBoard);
        return "noticeboard/edit";  
    }

    // 공지사항을 수정하는 메서드
    @PostMapping("/modify")
    public String updateNoticeBoard(@ModelAttribute NoticeBoardVO noticeBoard) {
        noticeBoardService.updateNoticeBoard(noticeBoard);
        return "redirect:/noticeboard/list";  
    }

    // 공지사항을 삭제하는 메서드
    @PostMapping("/delete")
    public String deleteNoticeBoard(@PathVariable("noticeBoardId") int noticeBoardId) {
        noticeBoardService.deleteNoticeBoard(noticeBoardId);
        return "redirect:/noticeboard/list";  
    }

}
