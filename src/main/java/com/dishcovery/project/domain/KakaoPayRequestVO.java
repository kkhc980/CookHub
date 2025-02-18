package com.dishcovery.project.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KakaoPayRequestVO {
    private String cid = "TC0ONETIME";  // 테스트용 가맹점 코드
    private String partner_order_id;
    private String partner_user_id;
    private String item_name;
    private int quantity;
    private int total_amount;
    private int tax_free_amount = 0;
    private String approval_url;
    private String cancel_url;
    private String fail_url;
}