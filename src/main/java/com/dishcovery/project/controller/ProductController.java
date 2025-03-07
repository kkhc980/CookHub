package com.dishcovery.project.controller;

import com.dishcovery.project.domain.*;
import com.dishcovery.project.service.MemberService;
import com.dishcovery.project.service.ProductService;
import com.dishcovery.project.util.PageMaker;
import com.dishcovery.project.util.Pagination;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/store")
@Log4j
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private HttpSession session;
    
    @GetMapping("/list")
    public String showProductList(@RequestParam(defaultValue = "1") int pageNum,
                                  @RequestParam(defaultValue = "4") int pageSize,
                                  Model model) {
        Pagination pagination = new Pagination(pageNum, pageSize);
        int totalCount = productService.getTotalProductCount();

        PageMaker pageMaker = new PageMaker();
        pageMaker.setPagination(pagination);
        pageMaker.setTotalCount(totalCount);

        List<ProductVO> productList = productService.getAllProducts(pagination);

        model.addAttribute("productList", productList);
        model.addAttribute("pagination", pagination);
        model.addAttribute("pageMaker", pageMaker);
        model.addAttribute("pageContent", "store/list.jsp");
        return "layout"; // 기존 방식 유지
    }
    
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        // ProductService에서 ingredients 목록 가져오기
        List<IngredientsVO> ingredientsList = productService.getAllProductIngredients();
        
        // JSP에서 사용할 모델에 추가
        model.addAttribute("ingredientsList", ingredientsList);

        return "store/register"; // register.jsp로 이동
    }
    
    @PostMapping("/register")
    public String registerProduct(@ModelAttribute ProductVO productVO, @RequestParam("productImage") MultipartFile file, Model model) {
        
        // 디버깅용 로그 출력
        System.out.println("등록 요청된 상품 정보: " + productVO);
        System.out.println("파일 업로드 요청: " + (file != null ? file.getOriginalFilename() : "파일 없음"));

        // 상품 등록 실행
        productService.insertProduct(productVO, file);
        return "redirect:/store/list";
    }

    @GetMapping("/detail/{productId}")
    public String moveDetailProduct(@PathVariable int productId, @AuthenticationPrincipal UserDetails userDetails, Model model) {
        log.info("moveDetailProduct");

        ProductVO productVO = productService.getProduct(productId);
        String email = userDetails.getUsername();
        MemberDTO memberDTO = memberService.getMemberByEmail(email);

        model.addAttribute("memberDTO", memberDTO);
        model.addAttribute("product", productVO);
        model.addAttribute("pageContent", "store/detail.jsp");

        return "layout";
    }

    // 장바구니에 상품 추가
    @PostMapping("/cart/add/{productId}")
    public String addToCart(@PathVariable int productId, @RequestParam("productCount") int productCount) {
        // 1. 상품 정보 가져오기
        ProductVO productVO = productService.getProduct(productId);

        // 2. OrderPageItemDTO 객체 생성 및 값 설정
        OrderPageItemDTO orderItem = new OrderPageItemDTO();
        orderItem.setProductId(productId);
        orderItem.setProductCount(productCount);
        orderItem.setProductName(productVO.getProductName());
        orderItem.setProductPrice(productVO.getProductPrice());
        orderItem.setStock(productVO.getStock());
        orderItem.initTotal(); // 총 가격 계산

        // 3. 세션에서 장바구니 목록 가져오기 (없으면 생성)
        List<OrderPageItemDTO> cart = (List<OrderPageItemDTO>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
        }

        // 4. 장바구니에 아이템 추가
        cart.add(orderItem);

        // 5. 세션에 장바구니 목록 저장
        session.setAttribute("cart", cart);

        // 6. 장바구니 페이지로 리다이렉트
        return "redirect:/store/cart";
    }

    // 장바구니 페이지 보여주기
    @GetMapping("/cart")
    public String showCart(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        MemberDTO memberDTO = memberService.getMemberByEmail(email);

        model.addAttribute("memberDTO", memberDTO);

        // 1. 세션에서 장바구니 목록 가져오기
        List<OrderPageItemDTO> cart = (List<OrderPageItemDTO>) session.getAttribute("cart");

        // 2. 장바구니가 비어있으면 메시지를 전달하고, 아니면 장바구니 내용을 전달한다.
        if (cart == null || cart.isEmpty()) {
            model.addAttribute("message", "장바구니가 비어있습니다.");
        } else {
            model.addAttribute("cart", cart);
        }
        model.addAttribute("pageContent", "store/cart.jsp");

        // 3. 장바구니 페이지로 이동한다.
        return "layout";
    }

    @GetMapping("/cart/update/{productId}")
    @ResponseBody
    public int getProductPrice(@PathVariable int productId) {
        ProductVO productVO = productService.getProduct(productId);
        return productVO.getProductPrice();
    }

    @GetMapping("/cart/delete/{productId}")
    public String deleteCartItem(@PathVariable int productId) {
        List<OrderPageItemDTO> cart = (List<OrderPageItemDTO>) session.getAttribute("cart");
        if (cart != null) {
            cart.removeIf(item -> item.getProductId() == productId);
            session.setAttribute("cart", cart);
        }
        return "redirect:/store/cart";
    }

    @PostMapping("/order/{memberId}")
    public String orderPage(@PathVariable("memberId") String memberId, @RequestParam("orders") List<String> orders, Model model) {
        // 1. OrderPageItemDTO 리스트 생성
        List<OrderPageItemDTO> orderList = new ArrayList<>();

        // 2. orders 파라미터를 순회하며 OrderPageItemDTO 객체 생성 및 값 설정
        for (String order : orders) {
            String[] parts = order.split(",");
            int productId = Integer.parseInt(parts[0]);
            int productCount = Integer.parseInt(parts[1]);

            // 상품 정보 가져오기
            ProductVO productVO = productService.getProduct(productId);

            OrderPageItemDTO orderItem = new OrderPageItemDTO();
            orderItem.setProductId(productId);
            orderItem.setProductCount(productCount);
            orderItem.setProductName(productVO.getProductName());
            orderItem.setProductPrice(productVO.getProductPrice());
            orderItem.setStock(productVO.getStock());
            orderItem.initTotal(); // 총 가격 계산

            orderList.add(orderItem);
        }

        // 3. OrderPageDTO 객체 생성 및 OrderPageItemDTO 리스트 설정
        OrderPageDTO orderPageDTO = new OrderPageDTO();
        orderPageDTO.setOrders(orderList);

        // 4. Model에 OrderPageDTO 객체와 memberId 추가
        model.addAttribute("orderPageDTO", orderPageDTO);
        model.addAttribute("memberId", memberId);
        model.addAttribute("pageContent", "store/order.jsp");

        return "layout";
    }
}
