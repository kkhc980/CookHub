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
    public String updateNoticeBoard(@PathVariable("noticeBoardId") int noticeBoardId, @ModelAttribute NoticeBoardVO noticeBoard) {
        noticeBoardService.updateNoticeBoard(noticeBoard);
        return "redirect:/noticeboard/list";
    }

    @PostMapping("/delete/{noticeBoardId}")
    public String deleteNoticeBoard(@PathVariable int noticeBoardId) {
        noticeBoardService.deleteNoticeBoard(noticeBoardId);
        return "redirect:/noticeboard/list";
    }
}