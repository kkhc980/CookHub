package com.dishcovery.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.log4j.Log4j;

@RequestMapping("/rankingboard")
@Controller
@Log4j
public class RecipeRankingController {
    /**
     * 기본 JSP 페이지로 이동 (ranklist.jsp)
     */
    @GetMapping("/ranklist")
    public String showRankListPage(Model model) {
    	 model.addAttribute("pageContent", "rankingboard/ranklist.jsp");

         // 공통 레이아웃 반환
         return "layout";
    }
}
