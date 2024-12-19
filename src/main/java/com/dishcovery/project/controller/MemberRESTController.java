package com.dishcovery.project.controller;

import com.dishcovery.project.config.RootConfig;
import com.dishcovery.project.config.SecurityConfig;
import com.dishcovery.project.domain.Member;
import com.dishcovery.project.service.MemberService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ContextConfiguration(classes = { RootConfig.class, SecurityConfig.class }) // 설정 파일 연결
@Log4j
public class MemberRESTController {
    @Autowired
    private MemberService memberService;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
    @PostMapping("/register")
    public ResponseEntity<String> registerMember(@RequestBody Member member) {
        try {
            memberService.registerMember(member);
            return new ResponseEntity<>("회원가입 성공", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("회원가입 실패: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
