package com.dishcovery.project.controller;

import com.dishcovery.project.config.MailAuthConfiguration;
import com.dishcovery.project.config.RootConfig;
import com.dishcovery.project.config.SecurityConfig;
import com.dishcovery.project.domain.Member;
import com.dishcovery.project.service.MailSendService;
import com.dishcovery.project.service.MemberService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@RestController
@ContextConfiguration(classes = {RootConfig.class, SecurityConfig.class, MailAuthConfiguration.class}) // 설정 파일 연결
@Log4j
public class MemberRESTController {
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

    // 회원 가입 처리
    @PostMapping("/member/signup")
    public void registerMember(Member member) {
        memberService.registerMember(member);

        //임의의 authKey 생성 & 이메일 발송
        String authKey = mss.sendAuthMail(member.getEmail());
        member.setAuthKey(authKey);

        Map<String, String> map = new HashMap<String, String>();
        map.put("email", member.getEmail());
        map.put("authKey", member.getAuthKey());
        System.out.println(map);

        //DB에 authKey 업데이트
        memberService.updateAuthKey(map);
    }

    // 메일 인증
    @GetMapping("/member/signUpConfirm")
    public ModelAndView signUpConfirm(@RequestParam Map<String, String> map, ModelAndView mav) {
        //email, authKey 가 일치할경우 authStatus 업데이트
        memberService.updateAuthStatus(map);

        mav.addObject("display", "/view/member/signUp_confirm.jsp");
        mav.setViewName("/view/index");
        return mav;
    }
}
