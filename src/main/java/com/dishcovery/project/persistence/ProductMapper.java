package com.dishcovery.project.persistence;

import java.util.List;

import com.dishcovery.project.domain.IngredientsVO;
import com.dishcovery.project.domain.ProductVO;
import com.dishcovery.project.util.Pagination;

public interface ProductMapper {

    // 상품 등록
    void insertProduct(ProductVO product);
    
    List<ProductVO> getAllProducts(Pagination pagination);
    
    int getTotalProductCount();
    
    // Ingredients 목록 조회
    List<IngredientsVO> getAllProductIngredients();
}
