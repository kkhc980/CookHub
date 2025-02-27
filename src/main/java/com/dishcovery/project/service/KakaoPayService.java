package com.dishcovery.project.service;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.dishcovery.project.domain.KakaoPayApproveVO;
import com.dishcovery.project.domain.KakaoPayRequestVO;
import com.dishcovery.project.domain.KakaoPayResponseVO;
import com.dishcovery.project.persistence.OrderMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KakaoPayService {
    private static final String SECRET_KEY = "DEV05190B5E41C1DFA247F81544067A6C7E53CFE"; // ✅ 카카오페이 API Key
    private static final String KAKAO_PAY_READY_URL = "https://open-api.kakaopay.com/online/v1/payment/ready";
    private static final String KAKAO_PAY_APPROVE_URL = "https://open-api.kakaopay.com/online/v1/payment/approve";
    
    private final OrderMapper orderMapper; // ✅ 주문 정보를 저장할 Mapper

    public KakaoPayResponseVO readyToPay(String productId, String productName, int totalPrice, int productCount, Integer memberId, HttpSession session) {
        RestTemplate restTemplate = new RestTemplate();

        if (memberId == null) {
            throw new RuntimeException("❌ 로그인된 사용자 정보를 찾을 수 없습니다.");
        }

        String partnerOrderId = "ORDER_" + System.currentTimeMillis(); // ✅ 주문 ID 생성

        KakaoPayRequestVO request = new KakaoPayRequestVO();
        request.setCid("TC0ONETIME");
        request.setPartner_order_id(partnerOrderId);
        request.setPartner_user_id(memberId.toString());
        request.setItem_name(productName);
        request.setQuantity(productCount);
        request.setTotal_amount(totalPrice);
        request.setApproval_url("http://localhost:8080/project/store/approve");
        request.setCancel_url("http://localhost:8080/project/store/cancel");
        request.setFail_url("http://localhost:8080/project/store/fail");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "SECRET_KEY " + SECRET_KEY);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<KakaoPayRequestVO> entity = new HttpEntity<>(request, headers);
        try {
            ResponseEntity<KakaoPayResponseVO> response = restTemplate.exchange(
                    KAKAO_PAY_READY_URL, HttpMethod.POST, entity, KakaoPayResponseVO.class);

            if (response.getBody() != null) {
                session.setAttribute("tid", response.getBody().getTid());
                session.setAttribute("member_id", memberId);
                session.setAttribute("partner_order_id", partnerOrderId);
            }
            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            System.err.println("🚨 카카오페이 API 오류: " + e.getResponseBodyAsString());
            throw new RuntimeException("❌ 결제 요청 중 오류가 발생했습니다.");
        } catch (Exception e) {
            System.err.println("🚨 알 수 없는 오류 발생: " + e.getMessage());
            throw new RuntimeException("❌ 결제 요청 처리 중 오류가 발생했습니다.");
        }
    }

    @Transactional
    public KakaoPayApproveVO approvePayment(String pg_token, HttpSession session) {
        RestTemplate restTemplate = new RestTemplate();

        String tid = (String) session.getAttribute("tid");
        String partnerOrderId = (String) session.getAttribute("partner_order_id");
        Integer memberId = (Integer) session.getAttribute("member_id");

        if (tid == null || partnerOrderId == null || memberId == null) {
            throw new RuntimeException("❌ 결제 정보가 유효하지 않습니다.");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "SECRET_KEY " + SECRET_KEY);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> params = new HashMap<>();
        params.put("cid", "TC0ONETIME");
        params.put("tid", tid);
        params.put("partner_order_id", partnerOrderId);
        params.put("partner_user_id", memberId.toString());
        params.put("pg_token", pg_token);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(params, headers);

        KakaoPayApproveVO approveVO = null;  // ✅ try 바깥에서 선언하여 catch에서도 접근 가능

        try {
            ResponseEntity<KakaoPayApproveVO> response = restTemplate.exchange(
                    KAKAO_PAY_APPROVE_URL, HttpMethod.POST, entity, KakaoPayApproveVO.class
            );

            approveVO = response.getBody();  // ✅ approveVO를 try 블록 바깥에서 사용 가능하게 변경

            if (approveVO == null || approveVO.getAid() == null) {
                throw new RuntimeException("❌ 결제 승인 실패: 승인번호 없음");
            }

            // ✅ 주문 중복 방지
            if (orderMapper.existsOrder(partnerOrderId) > 0) {
                throw new RuntimeException("❌ 이미 처리된 주문입니다.");
            }

            // ✅ 결제 승인 성공 → DB에 주문 정보 저장
            orderMapper.insertOrder(memberId, partnerOrderId, approveVO.getAmount().getTotal());

            System.out.println("✅ 결제 승인 성공: " + approveVO.getAid());
            return approveVO;

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            System.err.println("🚨 카카오페이 API 오류: " + e.getResponseBodyAsString());
            int cancelAmount = (approveVO != null) ? approveVO.getAmount().getTotal() : 0;  // ✅ approveVO 체크 후 사용
            cancelPayment(tid, cancelAmount, session);
            throw new RuntimeException("❌ 결제 승인 중 오류가 발생했습니다.");
        } catch (Exception e) {
            System.err.println("🚨 알 수 없는 오류 발생: " + e.getMessage());
            int cancelAmount = (approveVO != null) ? approveVO.getAmount().getTotal() : 0;  // ✅ approveVO 체크 후 사용
            cancelPayment(tid, cancelAmount, session);
            throw new RuntimeException("❌ 결제 승인 처리 중 오류가 발생했습니다.");
        }
    }



    public void cancelPayment(String tid, int cancelAmount, HttpSession session) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "SECRET_KEY " + SECRET_KEY);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> params = new HashMap<>();
        params.put("cid", "TC0ONETIME");
        params.put("tid", tid);
        params.put("cancel_amount", cancelAmount);  // ✅ 승인된 금액만큼 취소
        params.put("cancel_tax_free_amount", 0);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(params, headers);
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    "https://open-api.kakaopay.com/online/v1/payment/cancel",
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            System.out.println("✅ 결제 취소 완료: " + tid);
            System.out.println("🚀 카카오페이 응답: " + response.getBody());

            // ✅ 결제 취소 후 세션 정리
            session.removeAttribute("tid");
            session.removeAttribute("member_id");
            session.removeAttribute("partner_order_id");

        } catch (Exception e) {
            System.err.println("🚨 결제 취소 중 오류 발생: " + e.getMessage());
        }
    }
}
