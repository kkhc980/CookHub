<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>장바구니</title>
    <script src="https://code.jquery.com/jquery-3.7.1.js"></script>
</head>
<body>

<h2>장바구니</h2>

<c:if test="${not empty message}">
    <p>${message}</p>
</c:if>

<c:if test="${not empty cart}">
    <table>
        <thead>
        <tr>
            <th></th>
            <th>상품명</th>
            <th>가격</th>
            <th>수량</th>
            <th>총 가격</th>
            <th></th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="item" items="${cart}" varStatus="status">
            <tr>
                <td><input type="checkbox" checked="checked"></td>
                <td>${item.productName}</td>
                <td>${item.productPrice}</td>
                <td>
                    <div class="quantity-container">
                        <button class="quantity-btn minus-btn" data-index="${status.index}" data-product-id="${item.productId}">-</button>
                        <input type="number" class="quantity-input" value="${item.productCount}" min="1" max="${item.stock}" data-index="${status.index}" data-product-id="${item.productId}">
                        <button class="quantity-btn plus-btn" data-index="${status.index}" data-product-id="${item.productId}">+</button>
                        <!-- stock 값을 숨겨진 필드에 저장 -->
                    </div>
                </td>
                <td><span class="total-price" data-product-id="${item.productId}">${item.totalPrice}</span></td>
                <td><a href="../store/cart/delete/${item.productId}">삭제</a></td>
            </tr>
        </c:forEach>
        <tr>
            <td colspan="6">
                <div id="total-cart-price">
                    총 결제 금액: <span id="total-price-value">0</span> 원
                </div>
            </td>
        </tr>
        </tbody>
    </table>
</c:if>
<a href="../store/list">계속 쇼핑하기</a>
<a class="order_btn">주문하기</a>
<form class="order_form" action="${pageContext.request.contextPath}/store/order/${memberDTO.memberId}" method="post">
</form>

<script>
    $(document).ready(function() {
        // 수량 증가 버튼 클릭 이벤트
        $(".plus-btn").on("click", function() {
            let index = $(this).data("index");
            let productId = $(this).data("product-id");
            let quantityInput = $(".quantity-input[data-index='" + index + "']");
            let totalPriceSpan = $(".total-price[data-product-id='" + productId + "']");
            // 해당 상품의 stock 값을 가져옴
            let quantity = parseInt(quantityInput.val());
            let stock = parseInt(quantityInput.attr('max'));

            if (quantity < stock) {
                quantity++;
                quantityInput.val(quantity);
                updateTotalPrice(productId, quantity, totalPriceSpan); //총 가격 업데이트
            } else {
                alert("최대 구매 수량입니다.");
            }
        });

        // 수량 감소 버튼 클릭 이벤트
        $(".minus-btn").on("click", function() {
            let index = $(this).data("index");
            let productId = $(this).data("product-id");
            let quantityInput = $(".quantity-input[data-index='" + index + "']");
            let totalPriceSpan = $(".total-price[data-product-id='" + productId + "']");
            let quantity = parseInt(quantityInput.val());

            if (quantity > 1) {
                quantity--;
                quantityInput.val(quantity);
                updateTotalPrice(productId, quantity, totalPriceSpan); //총 가격 업데이트
            }
        });

        // 수량 직접 입력 이벤트
        $(".quantity-input").on("input", function() {
            let index = $(this).data("index");
            let productId = $(this).data("product-id");
            let quantityInput = $(".quantity-input[data-index='" + index + "']");
            let totalPriceSpan = $(".total-price[data-product-id='" + productId + "']");
            // 해당 상품의 stock 값을 가져옴
            let quantity = parseInt(quantityInput.val());
            let stock = parseInt(quantityInput.attr('max'));

            if (isNaN(quantity) || quantity < 1) {
                quantity = 1;
            } else if (quantity > stock) {
                quantity = stock;
                alert("최대 구매 수량을 초과했습니다.");
            }

            quantityInput.val(quantity);
            updateTotalPrice(productId, quantity, totalPriceSpan); //총 가격 업데이트
        });

        // 총 가격 업데이트 함수 (기존 코드에 통합)
        function updateTotalPrice(productId, quantity, totalPriceSpan) {
            $.ajax({
                url: "../store/cart/update/" + productId,
                type: "GET",
                success: function(price) {
                    let totalPrice = price * quantity;
                    totalPriceSpan.text(totalPrice);
                    calculateAndDisplayTotalCartPrice(); // 총 가격 업데이트 후 전체 총 가격 갱신
                },
                error: function() {
                    alert("가격 정보를 가져오는 데 실패했습니다.");
                }
            });
        }

        // 총 가격 계산 및 표시 함수
        function calculateAndDisplayTotalCartPrice() {
            let totalCartPrice = 0;
            $("input[type='checkbox']:checked").each(function() {
                let productId = $(this).closest("tr").find(".total-price").data("product-id");
                let totalPriceElement = $(".total-price[data-product-id='" + productId + "']");
                let totalPrice = parseInt(totalPriceElement.text());

                if (!isNaN(totalPrice)) {
                    totalCartPrice += totalPrice;
                }
            });
            $("#total-price-value").text(totalCartPrice);
        }

        // 체크박스 변경 이벤트 핸들러
        $("input[type='checkbox']").on("change", function() {
            calculateAndDisplayTotalCartPrice();
        });

        // 페이지 로드 시 초기 총 가격 계산
        calculateAndDisplayTotalCartPrice();

        $(".order_btn").on("click", function () {
            // 폼 비우기
            $(".order_form").empty();

            // 체크된 상품 정보 폼에 추가
            $("input[type='checkbox']:checked").each(function() {
                let productId = $(this).closest("tr").find(".quantity-input").data("product-id");
                let productCount = $(this).closest("tr").find(".quantity-input").val();

                // input hidden 필드 추가
                let orderValue = productId + "," + productCount;  // productId와 productCount를 쉼표로 구분
                let orderInput = $("<input type='hidden' name='orders' value='" + orderValue + "'>");

                $(".order_form").append(orderInput);
            });
            $(".order_form").submit(); // 폼 제출
        });
    });
</script>
</body>
</html>