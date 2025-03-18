package com.dishcovery.project.service;

import com.dishcovery.project.domain.IngredientsVO;
import com.dishcovery.project.domain.OrderHistoryDTO;
import com.dishcovery.project.domain.OrderPageItemDTO;
import com.dishcovery.project.domain.ProductVO;
import com.dishcovery.project.persistence.ProductMapper;
import com.dishcovery.project.util.Pagination;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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

    @Override
    public List<OrderPageItemDTO> getProductInfo(List<OrderPageItemDTO> orders) {
        List<OrderPageItemDTO> result = new ArrayList<OrderPageItemDTO>();

        for (OrderPageItemDTO ord : orders) {
            OrderPageItemDTO productInfo = productMapper.getProductInfo(ord.getProductId());
            productInfo.setProductCount(ord.getProductCount());
            productInfo.initTotal();

            result.add(productInfo);
        }

        return result;
    }

    @Override
    public List<Map<String, Object>> getOrderDetail(int memberId) {
        List<OrderHistoryDTO> orders = productMapper.getOrderDetail(memberId);
        Map<String, Map<String, List<OrderHistoryDTO>>> groupedOrders = new HashMap<>();
        List<Map<String, Object>> result = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (OrderHistoryDTO order : orders) {
            String orderId = order.getOrderId();
            LocalDateTime orderDate = order.getOrderDate();
            String key = orderId + "::" + orderDate.format(formatter);

            if (!groupedOrders.containsKey(key)) {
                Map<String, List<OrderHistoryDTO>> orderDetails = new HashMap<>();
                groupedOrders.put(key, orderDetails);
            }

            Map<String, List<OrderHistoryDTO>> orderDetails = groupedOrders.get(key);

            if (!orderDetails.containsKey(order.getOrderProductName())) {
                orderDetails.put(order.getOrderProductName(), new ArrayList<>());
            }
            orderDetails.get(order.getOrderProductName()).add(order);
        }

        for (Map.Entry<String, Map<String, List<OrderHistoryDTO>>> entry : groupedOrders.entrySet()) {
            String[] keyParts = entry.getKey().split("::");
            String orderId = keyParts[0];
            String orderDateStr = keyParts[1]; // 문자열 형태의 orderDate를 그대로 사용

            for (Map.Entry<String, List<OrderHistoryDTO>> productEntry : entry.getValue().entrySet()) {
                Map<String, Object> orderMap = new HashMap<>();
                orderMap.put("orderId", orderId);
                orderMap.put("orderDate", orderDateStr); // 문자열 형태의 orderDate를 그대로 사용
                orderMap.put("orderProductName", productEntry.getKey());
                orderMap.put("orderDetails", productEntry.getValue());

                int totalAmount = 0;
                for (OrderHistoryDTO order : productEntry.getValue()) {
                    totalAmount += order.getProductPrice() * order.getProductCount();
                }
                orderMap.put("totalAmount", totalAmount);

                result.add(orderMap);
            }
        }

        return result;
    }
}
