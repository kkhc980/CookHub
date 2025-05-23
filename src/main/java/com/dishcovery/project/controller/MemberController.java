package com.dishcovery.project.controller;

import com.dishcovery.project.domain.MemberDTO;
import com.dishcovery.project.domain.MemberVO;
import com.dishcovery.project.service.MailSendService;
import com.dishcovery.project.service.MemberService;
import com.dishcovery.project.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.security.SecureRandom;
import java.util.*;

@Controller
@RequestMapping("/member")
@Log4j
public class MemberController {
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final MailSendService mss;
    private final ProductService productService;

    @Autowired
    public MemberController(MemberService memberService, PasswordEncoder passwordEncoder, MailSendService mss, ProductService productService) {
        this.memberService = memberService;
        this.passwordEncoder = passwordEncoder;
        this.mss = mss;
        this.productService = productService;
    }

    // 이메일 중복 확인 처리
    @PostMapping("/emailCheck")
    public ResponseEntity<String> checkEmailDuplication(@RequestParam String email) {
        MemberVO memberVO = memberService.selectDupCheckEmail(email);

        if (memberVO != null) {
            if (memberVO.getAuthStatus() == 1) {
                return new ResponseEntity<>("registered", HttpStatus.OK); // 가입된 아이디
            } else {
                return new ResponseEntity<>("pending", HttpStatus.OK); // 이메일 인증 진행중
            }
        } else {
            return new ResponseEntity<>("ok", HttpStatus.OK); // 사용 가능
        }
    }

    // 회원 가입 페이지 이동
    @GetMapping("/signup")
    public String moveSignupPage(Model model) {
        log.info("moveSignupPage");
        model.addAttribute("pageContent", "member/signup.jsp");

        return "layout";
    }

    // 회원 가입 처리
    @PostMapping("/signup")
    public String registerMember(MemberDTO memberDTO, RedirectAttributes redirectAttributes) {
        // 임의의 authKey 생성
        String authKey = getKey(6);
        int result = signupMember(memberDTO, authKey);
        if (result == 1) {
            mss.sendAuthMail(memberDTO.getEmail(), authKey);

            // RedirectAttributes를 사용하여 메시지 전달
            redirectAttributes.addFlashAttribute("signupSuccess", true);
            return "redirect:/auth/login";
        }

        return "redirect:/member/signup";
    }

    // 회원 가입
    private int signupMember(MemberDTO memberDTO, String authKey) {
        memberDTO.setPassword(passwordEncoder.encode(memberDTO.getPassword()));
        int result = memberService.registerMember(memberDTO, authKey);

        return result;
    }

    // 메일 인증
    @GetMapping("/signUpConfirm")
    public String signUpConfirm(@RequestParam Map<String, String> map, RedirectAttributes redirectAttributes) {
        //email, authKey 가 일치하고, 인증 시간이 지나지 않았을 경우 expiresFlag 업데이트
        int result = memberService.updateExpiresFlag(map);
        if (result == 1) {
            memberService.updateAuthStatus(map.get("email"));
            memberService.deleteAuthKey(map.get("email"));
            return "redirect:/auth/login";
        } else {
            // RedirectAttributes 에 이메일 값을 저장
            redirectAttributes.addFlashAttribute("email", map.get("email"));
            return "redirect:/auth/login?error=expired";
        }
    }

    // 인증 번호 보내기
    @GetMapping("/mailCheck")
    @ResponseBody
    public String mailCheck(String email) {
        try {
            MemberVO memberVO = memberService.selectDupCheckEmail(email);
            if (memberVO == null) {
                // 등록되지 않은 이메일 주소
                Map<String, Object> response = new HashMap<>();
                response.put("result", "not_found");
                return new ObjectMapper().writeValueAsString(response);
            }

            String authKey = getKey(6);
            mss.sendVerificationCode(email, authKey);

            Map<String, Object> response = new HashMap<>();
            response.put("result", "success");
            response.put("authKey", authKey);
            return new ObjectMapper().writeValueAsString(response);

        } catch (Exception e) {
            log.error(e.getMessage());
            return "error";
        }
    }

    // 메일 인증 실패 후 메일 재 전송
    @GetMapping("/reAuth")
    public String reAuthKey(String email) {
        String authKey = getKey(6);
        int result = memberService.updateAuthKey(email, authKey);
        if (result == 1) {
            mss.sendAuthMail(email, authKey);
        }
        return "redirect:/auth/login";
    }

    // 메일 인증 실패 후 회원 정보 삭제
    @GetMapping("/deleteExpired")
    public String deleteExpired(String email) {
        int result = memberService.deleteMember(email);
        if (result == 1) {
            return "redirect:/auth/login?error=canceled";
        } else {
            return "redirect:/auth/login?error=true";
        }
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
                return "success";
            } else {
                return "fail";
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return "fail";
        }
    }

    // 인증 코드 생성
    private String getKey(int size) {
        Random random = new Random();
        StringBuffer buffer = new StringBuffer();
        int num = 0;

        while (buffer.length() < size) {
            num = random.nextInt(10);
            buffer.append(num);
        }

        return buffer.toString();
    }

    // 임시 비밀번호 생성
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

    // 회원 정보 페이지 이동
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

    // 회원 정보 수정 이동
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

    // 회원 정보 삭제
    @PostMapping("/delete")
    public String deleteMember(@AuthenticationPrincipal UserDetails userDetails, HttpSession session) {
        String email = userDetails.getUsername();
        int result = memberService.deleteMember(email);
        log.info(result + "row check delete");
        SecurityContextHolder.clearContext();
        session.invalidate();

        return "redirect:/recipeboard/list";
    }

    // 비밀 번호 찾기 페이지 이동
    @GetMapping("/findpw")
    public String moveFindPassword(Model model) {
        log.info("moveFindPassword");
        model.addAttribute("pageContent", "member/findpw.jsp");

        return "layout";
    }

    // 구매 내역 페이지
    @GetMapping("/orderdetail")
    public String showOrderDetailPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        String email = userDetails.getUsername();
        MemberDTO memberDTO = memberService.getMemberByEmail(email);

        List<Map<String, Object>> list = productService.getOrderDetail(memberDTO.getMemberId());

        model.addAttribute("list", list);
        model.addAttribute("pageContent", "member/history.jsp");

        return "layout";
    }
}
