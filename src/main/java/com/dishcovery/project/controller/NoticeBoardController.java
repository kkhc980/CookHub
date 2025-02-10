package com.dishcovery.project.controller;

import com.dishcovery.project.domain.NoticeBoardVO;
import com.dishcovery.project.service.NoticeBoardService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/noticeboard")
@Log4j
public class NoticeBoardController {

    @Autowired
    private NoticeBoardService noticeBoardService;

    // ��� ���������� ��ȸ�ϴ� �޼���
    @GetMapping("/list")
    public String getAllNoticeBoards(Model model) {
    	log.info("getAllNoticeBoards()");
        List<NoticeBoardVO> noticeBoardList = noticeBoardService.getAllNoticeBoards();
        model.addAttribute("noticeBoardList", noticeBoardList);
        model.addAttribute("pageContent", "noticeboard/list.jsp");

        // 공통 레이아웃 반환
        return "layout";  
    }

    // Ư�� ���������� ��ȸ�ϴ� �޼���
    @GetMapping("/detail/{noticeBoardId}")
    public String getNoticeBoardById(@PathVariable("noticeBoardId") int noticeBoardId, Model model) {
        NoticeBoardVO noticeBoard = noticeBoardService.getNoticeBoardById(noticeBoardId);
        model.addAttribute("NoticeBoardVO", noticeBoard); 
        return "noticeboard/detail";
    }

    // �������� ��� ���� �����ִ� �޼���
    @GetMapping("/register")
    public String showAddNoticeForm() {
        return "noticeboard/register";  
    }

    // ���ο� ���������� ����ϴ� �޼���
    @PostMapping("/register")
    public String addNoticeBoard(@ModelAttribute NoticeBoardVO noticeBoard) {
        noticeBoardService.addNoticeBoard(noticeBoard);
        return "redirect:/noticeboard/list";  
    }

    // �������� ���� ���� �����ִ� �޼���
    @GetMapping("/modify")
    public String showEditNoticeForm(@PathVariable("noticeBoardId") int noticeBoardId, Model model) {
        NoticeBoardVO noticeBoard = noticeBoardService.getNoticeBoardById(noticeBoardId);
        model.addAttribute("noticeBoard", noticeBoard);
        return "noticeboard/edit";  
    }

    // ���������� �����ϴ� �޼���
    @PostMapping("/modify")
    public String updateNoticeBoard(@ModelAttribute NoticeBoardVO noticeBoard) {
        noticeBoardService.updateNoticeBoard(noticeBoard);
        return "redirect:/noticeboard/list";  
    }

    // ���������� �����ϴ� �޼���
    @PostMapping("/delete/{noticeBoardId}")
    public String deleteNoticeBoard(@PathVariable int noticeBoardId) {
        noticeBoardService.deleteNoticeBoard(noticeBoardId);
        return "redirect:/noticeboard/list";
    }

}
