package com.dishcovery.project.service;

import com.dishcovery.project.domain.IngredientsVO;
import com.dishcovery.project.domain.ProductVO;
import com.dishcovery.project.persistence.ProductMapper;
import com.dishcovery.project.util.Pagination;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@Log4j
public class ProductServiceImple implements ProductService {
    
    @Autowired
    private ProductMapper productMapper;

    private static final String UPLOAD_DIR = "C:/uploads/product_images/"; // 변경된 업로드 경로

    @Override
    public void insertProduct(ProductVO productVO, MultipartFile file) {
        // 업로드 폴더가 없으면 자동 생성
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
            System.out.println("📁 업로드 디렉토리가 존재하지 않아 생성됨: " + UPLOAD_DIR);
        }

        if (file != null && !file.isEmpty()) {
            try {
                String originalFilename = file.getOriginalFilename();
                String storedFilename = UUID.randomUUID().toString() + "_" + originalFilename;

                // 파일 저장 경로 설정
                File dest = new File(UPLOAD_DIR + storedFilename);
                file.transferTo(dest);

                // DB에 저장할 파일 경로 설정
                productVO.setProductImagePath("product_images/" + storedFilename); // 상대 경로로 저장

                System.out.println("✅ 파일 업로드: " + originalFilename + " -> " + storedFilename);
                System.out.println("✅ 저장된 파일 경로: " + productVO.getProductImagePath());

            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("파일 업로드 중 오류 발생");
            }
        } else {
            System.out.println("⚠️ 파일이 업로드되지 않음.");
            productVO.setProductImagePath("product_images/default.png"); // 기본 이미지 설정
        }

        System.out.println("✅ DB 저장 전 productImagePath: " + productVO.getProductImagePath());

        // DB에 상품 등록
        productMapper.insertProduct(productVO);
    }

    @Override
    public List<ProductVO> getAllProducts(Pagination pagination) {
        return productMapper.getAllProducts(pagination);
    }

    @Override
    public int getTotalProductCount() {
        return productMapper.getTotalProductCount(); // 총 상품 개수 조회
    }
    
    @Override
    public List<IngredientsVO> getAllProductIngredients() {
        return productMapper.getAllProductIngredients();
    }

    @Override
    public ProductVO getProduct(int productId) {
        return productMapper.getProduct(productId);
    }
}
