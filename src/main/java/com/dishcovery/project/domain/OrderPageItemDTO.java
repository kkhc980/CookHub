package com.dishcovery.project.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class OrderPageItemDTO {
    // 페이지 에서 전달 받을 값
    private int productId;
    private int productCount;

    // DB 에서 꺼내올 값
    private String productName;
    private int productPrice;

    // 생성해 낼 값
    private int totalPrice;

    public void initTotal() {
        this.totalPrice = this.productPrice * this.productCount;
    }
}
