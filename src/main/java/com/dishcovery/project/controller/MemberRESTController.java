package com.dishcovery.project.controller;

import com.dishcovery.project.config.RootConfig;
import com.dishcovery.project.config.SecurityConfig;
import com.dishcovery.project.service.MemberService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@RestController
@ContextConfiguration(classes = { RootConfig.class, SecurityConfig.class }) // 설정 파일 연결
@Log4j
public class MemberRESTController {
    @Autowired
    private MemberService memberService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ajax 통신으로 아이디 중복확인 요청 처리용 메소드
    @RequestMapping(value = "emailCheck", method = RequestMethod.POST)
    public void dupCheckIdMethod(@RequestParam("email") String email, HttpServletResponse response) throws IOException {
        int idCount = memberService.selectDupCheckId(email);

        String returnStr = null;
        if (idCount == 0) {
            returnStr = "ok";
        } else {
            returnStr = "duple";
        }

        // response 를 이용해서 클라이언트와 출력스트림을 연결하고 값 보냄
        response.setContentType("text/html; charset=utf-8");
        PrintWriter out = response.getWriter();
        out.append(returnStr);
        out.flush();
        out.close();
    }


}
