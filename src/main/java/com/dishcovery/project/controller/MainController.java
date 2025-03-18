package com.dishcovery.project.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.dishcovery.project.domain.NoticeBoardVO;
import com.dishcovery.project.domain.RecipeBoardVO;
import com.dishcovery.project.service.NoticeBoardService;
import com.dishcovery.project.service.RecipeBoardService;

import lombok.extern.log4j.Log4j;

@Controller
@RequestMapping("/")
@Log4j
public class MainController {

    @Autowired
    private NoticeBoardService noticeBoardService;
    
    @Autowired
    private RecipeBoardService recipeBoardService;

    @GetMapping("/main")
    public String mainPage(Model model) {
        List<NoticeBoardVO> latestNotices = noticeBoardService.getLatestNotices(5);
        List<RecipeBoardVO> topPosts = recipeBoardService.getTopPosts();

        model.addAttribute("latestNotices", latestNotices);
        model.addAttribute("topPosts", topPosts);
        model.addAttribute("pageContent", "main.jsp");

        log.info("Loaded main page with latest notices and top posts.");
        
        return "layout";
    }
}
