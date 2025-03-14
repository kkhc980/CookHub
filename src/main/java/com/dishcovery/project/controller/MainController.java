package com.dishcovery.project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.log4j.Log4j;

@Controller
@RequestMapping("/")
@Log4j
public class MainController {

    @GetMapping("/main12321")
    public String mainPage(Model model) {
    	 model.addAttribute("pageContent", "main.jsp");

         // 공통 레이아웃 반환
         return "layout";
    }

}
