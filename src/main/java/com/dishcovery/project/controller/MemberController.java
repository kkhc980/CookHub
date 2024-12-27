package com.dishcovery.project.controller;

import com.dishcovery.project.service.MemberService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Log4j
public class MemberController {
    @Autowired
    private MemberService memberService;

    @GetMapping("/member/enroll")
    public String moveSignupPage(Model model) {
        model.addAttribute("message", "회원가입 페이지입니다.");
        return "/member/signup";
    }
}
