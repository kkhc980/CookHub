package com.dishcovery.project.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.dishcovery.project.domain.IngredientsVO;
import com.dishcovery.project.domain.ProductVO;

public interface ProductService {

	void insertProduct(ProductVO productVO, MultipartFile file);
    
    // Ingredients 목록 조회
    List<IngredientsVO> getAllProductIngredients();
}
