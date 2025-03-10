package com.dishcovery.project.persistence;

import org.apache.ibatis.annotations.Param;

public interface OrderMapper {
    
    void insertOrder(
        @Param("memberId") Integer memberId,
        @Param("orderId") String orderId,
        @Param("totalAmount") int totalAmount,
        @Param("productId") int productId, 
        @Param("productName") String productName, 
        @Param("productCount") int productCount);

    int existsOrder(@Param("orderId") String orderId);
}
