<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>장바구니</title>
    <script src="https://code.jquery.com/jquery-3.7.1.js"></script>
    <link rel="stylesheet" href="https://code.jquery.com/ui/1.13.2/themes/base/jquery-ui.css">
    <script src="https://code.jquery.com/ui/1.13.2/jquery-ui.js"></script>

    <style>
        body {
            font-family: sans-serif;
            margin: 20px;
        }

        h2 {
            color: #333;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 20px;
        }

        th, td {
            border: 1px solid #ddd;
            padding: 8px;
            text-align: center;
        }

        th {
            background-color: #f2f2f2;
        }

        .quantity-container {
            display: inline-flex;
            align-items: center;
        }

        .quantity-btn {
            background-color: #f0f0f0;
            border: 1px solid #ddd;
            padding: 5px 10px;
            cursor: pointer;
        }

        .quantity-input {
            width: 50px;
            text-align: center;
            padding: 5px;
            border: 1px solid #ddd;
        }

        .order_btn {
            background-color: #4CAF50;
            border: none;
            color: white;
            padding: 10px 20px;
            text-align: center;
            text-decoration: none;
            display: inline-block;
            font-size: 16px;
            cursor: pointer;
            border-radius: 5px;
            margin-top: 20px;
        }

        .shopping_btn {
            background-color: #008CBA;
            border: none;
            color: white;
            padding: 10px 20px;
            text-align: center;
            text-decoration: none;
            display: inline-block;
            font-size: 16px;
            cursor: pointer;
            border-radius: 5px;
            margin-top: 20px;
        }

        #total-cart-price {
            font-weight: bold;
            text-align: right;
            margin-top: 10px;
        }

        .message {
            color: green;
            font-weight: bold;
        }

        input[type="checkbox"] {
            transform: scale(1.5); /* 체크박스 크기 조정 */
        }

    </style>
</head>
<body>
<h2>장바구니</h2>
<c:if test="${not empty message}">
    <p class="message">${message}</p>
</c:if>
<c:if test="${not empty cart}">
    <table>
        <thead>
        <tr>
            <th><input type="checkbox" id="select-all"></th>
            <th>상품명</th>
            <th>가격</th>
            <th>수량</th>
            <th>합계</th>
            <th></th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="item" items="${cart}" varStatus="status">
            <tr>
                <td><input type="checkbox" checked="checked" class="item-checkbox"></td>
                <td>${item.productName}</td>
                <td><span class="product-price" data-product-id="${item.productId}">${item.productPrice}</span></td>
                <td>
                    <div class="quantity-container">
                        <button class="quantity-btn minus-btn" data-index="${status.index}"
                                data-product-id="${item.productId}">-
                        </button>
                        <input type="number" class="quantity-input" value="${item.productCount}" min="1"
                               max="${item.stock}" data-index="${status.index}" data-product-id="${item.productId}">
                        <button class="quantity-btn plus-btn" data-index="${status.index}"
                                data-product-id="${item.productId}">+
                        </button>
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
<a href="../store/list" class="shopping_btn">계속 쇼핑하기</a>
<a class="order_btn">주문하기</a>
<form class="order_form" action="${pageContext.request.contextPath}/store/order/${memberDTO.memberId}" method="post">
</form>
<script>
    $(document).ready(function () {
        // 수량 증가 버튼 클릭 이벤트
        $(".plus-btn").on("click", function () {
            updateQuantity($(this), 1);
        });
        // 수량 감소 버튼 클릭 이벤트
        $(".minus-btn").on("click", function () {
            updateQuantity($(this), -1);
        });
        // 수량 직접 입력 이벤트
        $(".quantity-input").on("input", function () {
            updateQuantity($(this), 0);
        });

        function updateQuantity(element, change) {
            let index = element.data("index");
            let productId = element.data("product-id");
            let quantityInput = $(".quantity-input[data-index='" + index + "']");
            let totalPriceSpan = $(".total-price[data-product-id='" + productId + "']");
            let quantity = parseInt(quantityInput.val());
            let stock = parseInt(quantityInput.attr('max'));
            if (change === 1 && quantity < stock) {
                quantity++;
            } else if (change === -1 && quantity > 1) {
                quantity--;
            } else if (change === 0) {
                if (isNaN(quantity) || quantity < 1) {
                    quantity = 1;
                } else if (quantity > stock) {
                    quantity = stock;
                    alert("최대 구매 수량을 초과했습니다.");
                }
            }
            quantityInput.val(quantity);
            updateTotalPrice(productId, quantity, totalPriceSpan);
        }

        // 총 가격 업데이트 함수
        function updateTotalPrice(productId, quantity, totalPriceSpan) {
            $.ajax({
                url: "../store/cart/update/" + productId,
                type: "GET",
                success: function (price) {
                    let totalPrice = price * quantity;
                    totalPriceSpan.text(totalPrice.toLocaleString());
                    updateProductPrice(productId, price);
                    calculateAndDisplayTotalCartPrice();
                },
                error: function () {
                    alert("가격 정보를 가져오는 데 실패했습니다.");
                }
            });
        }

        function updateProductPrice(productId, price) {
            $(".product-price[data-product-id='" + productId + "']").text(price.toLocaleString());
        }

        // 총 가격 계산 및 표시 함수
        function calculateAndDisplayTotalCartPrice() {
            let totalCartPrice = 0;
            $(".item-checkbox:checked").each(function () {
                let productId = $(this).closest("tr").find(".total-price").data("product-id");
                let totalPriceElement = $(".total-price[data-product-id='" + productId + "']");
                let totalPrice = parseInt(totalPriceElement.text().replace(/,/g, '').replace(" 원", ""));
                if (!isNaN(totalPrice)) {
                    totalCartPrice += totalPrice;
                }
            });
            $("#total-price-value").text(totalCartPrice.toLocaleString());
        }

        function initialize() {
            $(".product-price").each(function () {
                let productId = $(this).data("product-id");
                let price = parseInt($(this).text());
                updateProductPrice(productId, price);
            });

            $(".total-price").each(function () { // 초기 로딩 시 쉼표 추가
                let totalPrice = parseInt($(this).text().replace(/,/g, ''));
                $(this).text(totalPrice.toLocaleString() + " 원");
            });

            calculateAndDisplayTotalCartPrice();
        }

        initialize();

        // 전체 선택/해제 기능 추가
        $("#select-all").on("change", function () {
            $(".item-checkbox").prop("checked", $(this).prop("checked"));
            calculateAndDisplayTotalCartPrice();
        });
        // 체크 박스 변경 이벤트 핸들러 수정
        $(".item-checkbox").on("change", function () {
            calculateAndDisplayTotalCartPrice();
        });
        $(".order_btn").on("click", function () {
            $(".order_form").empty();
            $(".item-checkbox:checked").each(function () {
                let productId = $(this).closest("tr").find(".quantity-input").data("product-id");
                let productCount = $(this).closest("tr").find(".quantity-input").val();
                let orderValue = productId + ":" + productCount;
                let orderInput = $("<input type='hidden' name='orders' value='" + orderValue + "'>");
                $(".order_form").append(orderInput);
            });
            $(".order_form").submit();
        });
    });
</script>
</body>
</html>