package com.dishcovery.project.service;

import com.dishcovery.project.domain.MemberVO;
import com.dishcovery.project.persistence.MemberMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Autowired
    private MemberMapper memberMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String email = request.getParameter("email");

        MemberVO memberVO = null;
        try {
            memberVO = memberMapper.checkUser(email);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("../auth/login?error=true"); // 데이터베이스 오류 처리
            return;
        }

        if (memberVO == null) {
            response.sendRedirect("../auth/login?error=true"); // 사용자 정보 없음 처리
            return;
        }

        if (memberVO.getAuthStatus() == 0) {
            response.sendRedirect("../auth/login?error=authRequired");
        } else {
            response.sendRedirect("../auth/login?error=true"); // 그 외의 실패 처리
        }
    }
}