package com.dishcovery.project.persistence;

import org.apache.ibatis.annotations.Param;

public interface OrderMapper {
    
    void insertOrder(
        @Param("memberId") Integer memberId,
        @Param("orderId") String orderId,
        @Param("totalAmount") int totalAmount,
        @Param("productName") String productName,
        @Param("postcode") String postcode,
        @Param("address") String address);

    int existsOrder(@Param("orderId") String orderId);
}
