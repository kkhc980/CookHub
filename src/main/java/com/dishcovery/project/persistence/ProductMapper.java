package com.dishcovery.project.persistence;

import com.dishcovery.project.domain.*;
import com.dishcovery.project.util.Pagination;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductMapper {

    // 상품 등록
    void insertProduct(ProductVO product);

    List<ProductVO> getAllProducts(Pagination pagination);

    int getTotalProductCount();

    // Ingredients 목록 조회
    List<IngredientsVO> getAllProductIngredients();

    ProductVO getProduct(int productId);

    OrderPageItemDTO getProductInfo(int productId);

    int insertProductDetails(@Param("memberId") int memberId, @Param("orderId") String orderId, @Param("orderDetails") List<OrderPageItemDTO> orderDetails);

    List<OrderHistoryDTO> getOrderDetail(int memberId);
}
