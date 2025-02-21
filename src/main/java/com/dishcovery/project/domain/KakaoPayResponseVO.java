package com.dishcovery.project.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KakaoPayResponseVO {
    private String tid;                 // 결제 고유번호
    private String next_redirect_pc_url; // PC 웹 결제 URL
    private String created_at;
}