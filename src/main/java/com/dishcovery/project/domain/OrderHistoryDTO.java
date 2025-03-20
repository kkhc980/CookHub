package com.dishcovery.project.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class OrderHistoryDTO {
    private String orderId;
    private LocalDateTime orderDate;
    private int totalAmount;
    private String orderProductName;
    private Long productId;
    private int productCount;
    private String productName;
    private int productPrice;
    private int productTotalPrice; // 상품 총 가격 필드
    private String address;

    public int getProductTotalPrice() {
        return productPrice * productCount; // 상품 총 가격 계산 함수
    }
}
