package com.dishcovery.project.domain;

import lombok.Data;

@Data
public class KakaoPayApproveVO {
    private String aid;                     // 결제 승인 번호
    private String tid;                     // 결제 고유 번호
    private String cid;                     // 가맹점 코드
    private String sid;                     // 정기 결제용 ID (단건 결제에서는 사용 X)
    private String partner_order_id;        // 가맹점 주문번호
    private String partner_user_id;         // 가맹점 회원 ID
    private String payment_method_type;     // 결제 수단 (CARD, MONEY 등)
    private Amount amount;                  // 결제 금액 정보
    private String item_name;               // 상품명
    private String created_at;              // 결제 요청 시간
    private String approved_at;             // 결제 승인 시간

    @Data
    public static class Amount {
        private int total;       // 총 결제 금액
        private int tax_free;    // 비과세 금액
        private int vat;         // 부가세
        private int point;       // 포인트 사용 금액
        private int discount;    // 할인 금액
    }
}
