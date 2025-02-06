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
        return "noticeboard/list";  
    }

    // Ư�� ���������� ��ȸ�ϴ� �޼���
    @GetMapping("/view")
    public String getNoticeBoardById(@PathVariable("noticeBoardId") int noticeBoardId, Model model) {
        NoticeBoardVO noticeBoard = noticeBoardService.getNoticeBoardById(noticeBoardId);
        model.addAttribute("noticeBoard", noticeBoard);
        return "noticeboard/view";  
    }

    // �������� ��� ���� �����ִ� �޼���
    @GetMapping("/register")
    public String showAddNoticeForm() {
        return "noticeboard/add";  
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
    @PostMapping("/delete")
    public String deleteNoticeBoard(@PathVariable("noticeBoardId") int noticeBoardId) {
        noticeBoardService.deleteNoticeBoard(noticeBoardId);
        return "redirect:/noticeboard/list";  
    }

}
