<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>상품 상세 정보</title>
    <script src="https://code.jquery.com/jquery-3.7.1.js"></script>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            margin: 20px;
            background-color: #f8f8f8;
        }

        .product-container {
            display: flex;
            justify-content: center;
            max-width: 800px;
            margin: 0 auto;
            background-color: #fff;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
        }

        .product-image-container {
            flex: 1;
            padding-right: 20px;
            display: flex;
            justify-content: center;
            border-right: 1px solid #eee;
        }

        .product-image {
            max-width: 100%;
            max-height: 400px; /* 이미지 높이 증가 */
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }

        .product-info-container {
            flex: 1;
            padding-left: 20px;
            display: flex; /* flexbox 설정 */
            flex-direction: column; /* 세로 방향으로 요소 배치 */
            justify-content: center; /* 세로 가운데 정렬 */
        }

        .product-info {
            text-align: center; /* 글씨 가운데 정렬 */
        }

        .product-info p {
            margin: 10px 0;
            color: #333;
            text-align: center; /* 글씨 가운데 정렬 */
        }

        .button {
            display: flex;
            flex-direction: column;
            align-items: center; /* 버튼 가운데 정렬 */
            margin-top: 20px;
        }

        .button_quantity {
            display: flex;
            align-items: center;
            margin-bottom: 10px;
        }

        .quantity_input {
            width: 50px;
            text-align: center;
            padding: 8px;
            margin: 0 10px;
            border: 1px solid #ddd;
            border-radius: 4px;
        }

        .minus_btn, .plus_btn {
            background-color: #f0f0f0;
            border: 1px solid #ddd;
            padding: 8px 12px;
            cursor: pointer;
            border-radius: 4px;
        }

        .btn_cart, .purchase-button {
            background-color: #4CAF50;
            border: none;
            color: white;
            padding: 12px 24px;
            text-align: center;
            text-decoration: none;
            display: inline-block;
            font-size: 16px;
            cursor: pointer;
            border-radius: 4px;
            margin: 5px;
            transition: background-color 0.3s;
        }

        .btn_cart {
            background-color: #008CBA;
        }

        .btn_cart:hover {
            background-color: #0077b3;
        }

        .purchase-button:hover {
            background-color: #3e8e41;
        }

        .sold-out-button {
            background-color: grey;
            border: none;
            color: white;
            padding: 12px 24px;
            text-align: center;
            text-decoration: none;
            display: inline-block;
            font-size: 16px;
            cursor: not-allowed;
            border-radius: 4px;
            margin: 5px;
        }
    </style>
</head>
<body>
<div class="product-container">
    <div class="product-image-container">
        <img src="${pageContext.request.contextPath}/uploads/${product.productImagePath}"
             alt="상품 이미지"
             onerror="이미지 없음"
             class="product-image">
    </div>
    <div class="product-info-container">
        <div class="product-info">
            <p>상품명 : ${product.productName}</p>
            <p>가격 : <span id="product-price">${product.productPrice}</span>원</p>
            <p>총 가격 : <span id="total-price">${product.productPrice}</span>원</p>
            <p>재고 : ${product.stock}개</p>
        </div>
        <div id="sold-out" style="display: none;">
            <button class="sold-out-button">품절</button>
        </div>
        <div class="button">
            <div class="button_quantity">
                주문수량 &nbsp;
                <span>
                    <button class="minus_btn">-</button>
                </span>
                <input type="text" class="quantity_input" value="1">
                <span>
                  <button class="plus_btn">+</button>
              </span>
            </div>
            <div class="button_set">
                <button class="btn_cart">장바구니 추가</button>
                <button class="purchase-button"
                        onclick="purchaseProduct('${product.productId}', '${product.productName}', ${product.productPrice})">
                    구매하기
                </button>
            </div>
        </div>
    </div>
</div>

<script src="https://code.jquery.com/jquery-3.7.1.js"></script>
<script>
    // 초기 가격 설정
    let stock = ${product.stock};
    let productPrice = ${product.productPrice};
    let quantity = $(".quantity_input").val();
    updateTotalPrice();
    updateProductPrice();

    // 상품 가격 업데이트 함수
    function updateProductPrice() {
        let formattedPrice = productPrice.toLocaleString();
        $("#product-price").text(formattedPrice);
    }

    // 총 가격 업데이트 함수
    function updateTotalPrice() {
        let totalPrice = productPrice * quantity;
        let formattedTotalPrice = totalPrice.toLocaleString();
        $("#total-price").text(formattedTotalPrice);
    }

    // 수량 증가 버튼
    $(".plus_btn").on("click", function () {
        if (quantity < stock) {
            $(".quantity_input").val(++quantity);
            updateTotalPrice();
        } else {
            alert("재고량이 부족합니다.");
        }
    });

    // 수량 감소 버튼
    $(".minus_btn").on("click", function () {
        if (quantity > 1) {
            $(".quantity_input").val(--quantity);
            updateTotalPrice();
        }
    });

    // 입력값 제한 및 가격 업데이트
    $(".quantity_input").on("input", function () {
        let value = $(this).val();
        if (!/^[0-9]*$/.test(value)) {
            alert("숫자만 입력하세요.");
            $(this).val(value.replace(/[^0-9]/g, ''));
        }

        quantity = parseInt($(this).val()) || 1;

        if (quantity > stock) {
            alert("재고량이 부족합니다.");
            quantity = stock;
        }
        if (quantity < 1) {
            quantity = 1;
        }
        $(this).val(quantity);
        updateTotalPrice();
    });

    // 장바구니 버튼
    $(".btn_cart").on("click", function () {
        let productCount = $(".quantity_input").val();

        if (productCount > stock) {
            alert("재고량이 부족합니다.");
            return;
        }

        $.ajax({
            type: "POST",
            url: "../cart/add/${product.productId}",
            data: {productCount: productCount},
            success: function (response) {
                if (response === "ok") {
                    alert("장바구니에 추가되었습니다!");
                    window.location.href = "../cart";
                } else if (response === "duplicate") {
                    alert("이미 장바구니에 있는 상품입니다.");
                } else {
                    alert("오류가 발생했습니다.");
                }
            },
            error: function (error) {
                alert("오류가 발생했습니다.");
            }
        });
    });

    // 바로구매 버튼
    function purchaseProduct(productId, productName) {
        let productCount = $(".quantity_input").val();
        if (productCount > stock) {
            alert("재고량이 부족합니다.");
            return;
        }

        let form = document.createElement("form");
        form.method = "POST";
        form.action = '${pageContext.request.contextPath}/store/order/${memberDTO.memberId}'; // order로 전송

        // csrf 토큰 추가
        let csrfInput = document.createElement("input");
        csrfInput.type = "hidden";
        csrfInput.name = "${_csrf.parameterName}";
        csrfInput.value = "${_csrf.token}";
        form.appendChild(csrfInput);

        // 주문 상품 정보 추가
        let orderValue = productId + ":" + productCount;
        let orderInput = document.createElement("input");
        orderInput.type = "hidden";
        orderInput.name = "orders";
        orderInput.value = orderValue;
        form.appendChild(orderInput);

        document.body.appendChild(form);
        form.submit();
    }

    // 숫자만 입력하도록 제한 및 재고량 체크
    $(".quantity_input").on("input", function () {
        let value = $(this).val();
        if (!/^[0-9]*$/.test(value)) { // 정규식으로 숫자만 허용
            alert("숫자만 입력하세요.");
            $(this).val(value.replace(/[^0-9]/g, '')); // 숫자 이외의 문자 제거
        }

        quantity = parseInt($(this).val()) || 1; // 현재 quantity 업데이트

        if (quantity > stock) {
            alert("재고량이 부족합니다.");
            $(this).val(stock);
            quantity = stock;
        }

        if (quantity < 1) {
            quantity = 1;
            $(this).val(1);
        }
    });

    // 재고 상태 확인 및 품절 버튼 업데이트 함수
    function updateSoldOutStatus() {
        if (stock <= 0) {
            $("#sold-out").show();
            $(".button").hide(); // 기존 버튼 숨김
        } else {
            $("#sold-out").hide();
            $(".button").show(); // 기존 버튼 표시
        }
    }

    // 초기 로딩 시 품절 상태 확인
    updateSoldOutStatus();

    // 수량 변경 시 품절 상태 업데이트
    $(".plus_btn, .minus_btn, .quantity_input").on("click input", function () {
        updateSoldOutStatus();
    });

    // 품절 버튼 클릭 이벤트
    $("#sold-out").on("click", function () {
        alert("품절된 상품입니다.");
    });
</script>
</body>
</html>