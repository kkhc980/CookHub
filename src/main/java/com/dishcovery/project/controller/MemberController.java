package com.dishcovery.project.controller;

import com.dishcovery.project.domain.MemberDTO;
import com.dishcovery.project.service.MailSendService;
import com.dishcovery.project.service.MemberService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
    public String moveSignupPage(Model model) {
        log.info("moveSignupPage");
        model.addAttribute("pageContent", "member/signup.jsp");

        return "layout";
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

    // 메일 인증
    @GetMapping("/signUpConfirm")
    public String signUpConfirm(@RequestParam Map<String, String> map) {
        //email, authKey 가 일치할경우 authStatus 업데이트
        memberService.updateAuthStatus(map);
        return "redirect:/auth/login";
    }

    @GetMapping("/detail")
    public String moveMemberDetailPage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        // @AuthenticationPrincipal : 인증된 사용자의 Principal을 주입
        log.info("moveDetailPage");
        String email = userDetails.getUsername();
        MemberDTO memberDTO = memberService.getMemberByEmail(email);
        model.addAttribute("memberDTO", memberDTO);
        model.addAttribute("pageContent", "member/detail.jsp");

        return "layout";
    }

    @GetMapping("/update")
    public String moveMemberUpdatePage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        log.info("moveUpdatePage");
        String email = userDetails.getUsername();
        MemberDTO memberDTO = memberService.getMemberByEmail(email);
        model.addAttribute("memberDTO", memberDTO);
        model.addAttribute("pageContent", "member/update.jsp");

        return "layout";
    }

    // modify.jsp에서 전송된 회원 정보로 데이터 수정
    @PreAuthorize("principal.username == #memberDTO.email")
    @PostMapping("/update")
    public String modifyPOST(MemberDTO memberDTO) {
        log.info("modifyPOST()");
        String encPw = passwordEncoder.encode(memberDTO.getPassword());
        memberDTO.setPassword(encPw);
        int result = memberService.updateMember(memberDTO);
        log.info(result + "row update");
        return "redirect:/member/detail";
    }

    @PostMapping("/delete")
    public String deleteMember(@AuthenticationPrincipal UserDetails userDetails, HttpSession session) {
        String email = userDetails.getUsername();
        int result = memberService.deleteMember(email);
        log.info(result + "row check delete");
        SecurityContextHolder.clearContext();
        session.invalidate();
        return "redirect:/recipeboard/list";
    }
}
