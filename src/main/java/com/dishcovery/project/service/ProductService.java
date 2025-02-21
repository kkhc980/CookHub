package com.dishcovery.project.service;

import com.dishcovery.project.domain.IngredientsVO;
import com.dishcovery.project.domain.ProductVO;
import com.dishcovery.project.util.Pagination;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {

	void insertProduct(ProductVO productVO, MultipartFile file);
    
	List<ProductVO> getAllProducts(Pagination pagination);
	int getTotalProductCount();
    // Ingredients 목록 조회
    List<IngredientsVO> getAllProductIngredients();
	ProductVO getProduct(int productId);
}
