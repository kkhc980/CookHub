package com.dishcovery.project.persistence;

import java.util.List;

import com.dishcovery.project.domain.IngredientsVO;
import com.dishcovery.project.domain.ProductVO;

public interface ProductMapper {

    // 상품 등록
    void insertProduct(ProductVO product);
    
    // Ingredients 목록 조회
    List<IngredientsVO> getAllProductIngredients();
}
