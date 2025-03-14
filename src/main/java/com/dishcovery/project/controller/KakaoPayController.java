package com.dishcovery.project.controller;

import javax.servlet.http.HttpSession;

import com.dishcovery.project.domain.OrderPageDTO;
import lombok.extern.log4j.Log4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.dishcovery.project.domain.CustomUser;
import com.dishcovery.project.domain.KakaoPayApproveVO;
import com.dishcovery.project.domain.KakaoPayResponseVO;
import com.dishcovery.project.service.KakaoPayService;

@Controller
@RequestMapping("/store")
@Log4j
public class KakaoPayController {
    private final KakaoPayService kakaoPayService;

    public KakaoPayController(KakaoPayService kakaoPayService) {
        this.kakaoPayService = kakaoPayService;
    }

    // ✅ 결제 요청 (카카오페이 결제창으로 리디렉트)
    @PostMapping("/purchase")
    public RedirectView kakaoPayReady(OrderPageDTO orderPageDTO, @RequestParam("totalPayment") int totalPayment, HttpSession session) {
        Integer memberId = getCurrentUserId();
        if (memberId == null) {
            throw new RuntimeException("로그인된 사용자 정보를 찾을 수 없습니다.");
        }

        if (orderPageDTO == null || orderPageDTO.getOrders() == null || orderPageDTO.getOrders().isEmpty()) {
            throw new IllegalArgumentException("주문 정보가 없습니다.");
        }

        log.info("orderPageDTO : " + orderPageDTO);

        KakaoPayResponseVO response = kakaoPayService.readyToPay(orderPageDTO, totalPayment, memberId, session);

        if (response == null || response.getNext_redirect_pc_url() == null) {
            throw new RuntimeException("카카오페이 결제 요청 중 오류가 발생했습니다.");
        }

        return new RedirectView(response.getNext_redirect_pc_url());
    }

    // ✅ 결제 승인 (카카오페이에서 GET 요청으로 호출)
    @GetMapping("/approve")
    public ModelAndView approvePayment(@RequestParam("pg_token") String pg_token, HttpSession session) {
        KakaoPayApproveVO approveResponse = kakaoPayService.approvePayment(pg_token, session);

        ModelAndView mv = new ModelAndView("paymentResult");

        if (approveResponse != null && approveResponse.getAid() != null) {
            mv.addObject("message", "✅ 결제가 성공적으로 완료되었습니다.");
            mv.addObject("result", approveResponse);
        } else {
            mv.addObject("message", "❌ 결제가 실패하였습니다. 다시 시도해주세요.");
            mv.addObject("result", null);
        }

        // ✅ 세션 정리
        session.removeAttribute("tid");
        session.removeAttribute("member_id");
        session.removeAttribute("partner_order_id");
        session.removeAttribute("product_id");
        session.removeAttribute("product_name");  // ✅ 추가
        session.removeAttribute("product_count");
        return mv;
    }

    // ✅ 결제 취소 시 처리
    @GetMapping("/cancel")
    public ModelAndView cancelPayment() {
        ModelAndView mv = new ModelAndView("paymentResult");
        mv.addObject("message", "🚫 결제가 취소되었습니다.");
        return mv;
    }

    // ✅ 결제 실패 시 처리
    @GetMapping("/fail")
    public ModelAndView failPayment() {
        ModelAndView mv = new ModelAndView("paymentResult");
        mv.addObject("message", "❌ 결제가 실패하였습니다. 다시 시도해주세요.");
        return mv;
    }

    // ✅ 로그인된 사용자 ID 가져오는 메서드
    private Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof CustomUser) {
            CustomUser customUser = (CustomUser) authentication.getPrincipal();
            return customUser.getMemberVO().getMemberId(); // ✅ CustomUser에서 memberId 가져오기
        }
        return null; // 인증되지 않은 경우 null 반환
    }
}
