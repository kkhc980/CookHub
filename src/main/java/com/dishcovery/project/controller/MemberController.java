package com.dishcovery.project.controller;

import com.dishcovery.project.domain.MemberDTO;
import com.dishcovery.project.domain.MemberVO;
import com.dishcovery.project.service.MailSendService;
import com.dishcovery.project.service.MemberService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/member")
@Log4j
public class MemberController {
    @Autowired
    private MemberService memberService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MailSendService mss;

    // 이메일 중복 확인 처리
    @PostMapping("/emailCheck")
    public ResponseEntity<String> checkEmailDuplication(@RequestParam String email) {
        boolean isDuplicate = memberService.selectDupCheckEmail(email);
        if (isDuplicate) {
            return new ResponseEntity<>("fail", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("ok", HttpStatus.OK);
        }
    }

    @GetMapping("/signup")
    public void moveSignupPage() {
        log.info("moveSignupPage");
    }

    // 회원 가입 처리
    @PostMapping("/signup")
    public String registerMember(MemberDTO memberDTO) {
        String password = memberDTO.getPassword();
        memberDTO.setPassword(passwordEncoder.encode(password));
        int result = memberService.registerMember(memberDTO);
        log.info(result + "행 등록");

        //임의의 authKey 생성 & 이메일 발송
        String authKey = mss.sendAuthMail(memberDTO.getEmail());
        memberDTO.setAuthKey(authKey);

        Map<String, String> map = new HashMap<String, String>();
        map.put("email", memberDTO.getEmail());
        map.put("authKey", memberDTO.getAuthKey());
        System.out.println(map);

        //DB에 authKey 업데이트
        int updateResult = memberService.updateAuthKey(map);
        log.info(updateResult + "행 수정");

        return "redirect:/auth/login";
    }

    @GetMapping("/login")
    public void moveLoginPage() {
        log.info("moveLoginPage");
    }

    // 로그인 패스워드 비교
    @PostMapping(value = "/login")
    public String loginMethod(@RequestParam String email, @RequestParam String password, HttpSession session) {
        MemberVO loginMember = memberService.getMemberByEmail(email);
        if (loginMember != null && loginMember.getAuthStatus() == 1 && passwordEncoder.matches(password, loginMember.getPassword())) {
            session.setAttribute("loginMember", loginMember);
            System.out.println("loginMember : " + loginMember);
            System.out.println("로그인 성공");
            return "/";
        } else {
            System.out.println("로그인 실패");
            return "/member/login";
        }
    }

    // 메일 인증
    @GetMapping("/signUpConfirm")
    public String signUpConfirm(@RequestParam Map<String, String> map) {
        //email, authKey 가 일치할경우 authStatus 업데이트
        memberService.updateAuthStatus(map);
        return "redirect:/auth/login";
    }

    @GetMapping("/update")
    public String moveMemberUpdatePage() {
        return "/member/update";
    }
}
