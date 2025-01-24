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
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/member")
@Log4j
public class MemberController {
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final MailSendService mss;

    @Autowired
    public MemberController(MemberService memberService, PasswordEncoder passwordEncoder, MailSendService mss) {
        this.memberService = memberService;
        this.passwordEncoder = passwordEncoder;
        this.mss = mss;
    }

    // 이메일 중복 확인 처리
    @PostMapping("/emailCheck")
    public ResponseEntity<String> checkEmailDuplication(@RequestParam String email) {
        return memberService.selectDupCheckEmail(email) ?
                new ResponseEntity<>("fail", HttpStatus.OK) :
                new ResponseEntity<>("ok", HttpStatus.OK);
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
        // 임의의 authKey 생성
        String authKey = mss.getKey(6);
        int result = signupMember(memberDTO, authKey);
        if (result == 1) {
            mss.sendAuthMail(memberDTO.getEmail(), authKey);
            return "redirect:/auth/login";
        }

        return "redirect:/member/signup";
    }

    private int signupMember(MemberDTO memberDTO, String authKey) {
        memberDTO.setPassword(passwordEncoder.encode(memberDTO.getPassword()));
        memberDTO.setAuthKey(authKey);

        int result = memberService.registerMember(memberDTO);
        log.info(result + "행 등록");

        return result;
    }

    // 메일 인증
    @GetMapping("/signUpConfirm")
    public String signUpConfirm(@RequestParam Map<String, String> map) {
        //email, authKey 가 일치할경우 authStatus 업데이트
        memberService.updateAuthStatus(map);
        return "redirect:/auth/login";
    }

    // 인증 번호 보내기
    @GetMapping("/mailCheck")
    @ResponseBody
    public String mailCheck(String email) {
        log.info("mailCheck");
        String authKey = mss.getKey(6);
        Map<String, String> map = new HashMap<>();
        map.put("email", email);
        map.put("authKey", authKey);
        int result = memberService.updateAuthKey(map);
        log.info(result + " row update");
        mss.sendVerificationCode(email, authKey);
        log.info("mail send");
        return authKey;
    }

    // 임시 비밀번호 발송
    @PostMapping("/sendTempPassword")
    @ResponseBody
    public String sendTempPassword(@RequestParam("email") String email) {
        try {
            // 임시 비밀번호 생성
            String tempPw = getRandomPassword(8);
            Map<String, String> map = new HashMap<>();
            map.put("email", email);
            map.put("tempPw", passwordEncoder.encode(tempPw));
            int result = memberService.updateTempPw(map);

            if (result > 0) {
                mss.sendTemporaryPassword(email, tempPw);
                log.info("임시 비밀번호 발송 완료");
                return "success";
            } else {
                log.info("임시 비밀번호 발송 실패");
                return "fail";
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return "fail";
        }
    }

    private String getRandomPassword(int size) {
        char[] charSet = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
                'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b',
                'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
                'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '!', '@', '#', '$',
                '%', '^', '&'
        };
        StringBuffer sb = new StringBuffer();
        SecureRandom sr = new SecureRandom();
        sr.setSeed(new Date().getTime());

        int idx = 0;
        int len = charSet.length;
        for (int i = 0; i < size; i++) {
            idx = sr.nextInt(len);

            sb.append(charSet[idx]);
        }

        return sb.toString();
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

    @GetMapping("/findpw")
    public String moveFindPassword(Model model) {
        log.info("moveFindPassword");
        model.addAttribute("pageContent", "member/findpw.jsp");

        return "layout";
    }

    @GetMapping("/updatepw")
    public String moveUpdatePassword(Model model) {
        log.info("moveUpdatePassword");
        model.addAttribute("pageContent", "member/updatepw.jsp");

        return "layout";
    }
}
